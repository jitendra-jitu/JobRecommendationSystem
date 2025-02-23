package com.techstart.jobportal.controller;

import com.techstart.jobportal.dto.JobRecommendation;
import com.techstart.jobportal.model.*;
import com.techstart.jobportal.repository.JobRepository;
import com.techstart.jobportal.repository.UserInteractionRepository;
import com.techstart.jobportal.repository.UserRepository;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommendations")
public class ContentBasedRecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(ContentBasedRecommendationController.class);

    // Adjusted Constants for interaction weights
    private static final double SEARCH_QUERY_WEIGHT = 1.1;
    private static final double APPLIED_JOB_BASE_WEIGHT = 0.5;
    private static final double APPLIED_JOB_BOOST = 1.0;
    private static final double LIKE_WEIGHT = 1.0;
    private static final double DISLIKE_WEIGHT = -1.0;
    private static final double COMMENT_WEIGHT = 0.8;
    private static final double TITLE_TOKEN_WEIGHT = 1.0;
    private static final double SKILLS_TOKEN_WEIGHT = 0.5;
    private static final double DESCRIPTION_TOKEN_WEIGHT = 0.4;

    // Dynamic thresholds based on interaction count
    private static final double SCORE_THRESHOLD_LOW = 0.3;
    private static final double SCORE_THRESHOLD_HIGH = 0.5;

    private final UserRepository userRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final JobRepository jobRepository;

    public ContentBasedRecommendationController(UserRepository userRepository,
                                                UserInteractionRepository userInteractionRepository,
                                                JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.userInteractionRepository = userInteractionRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/content-based")
    public List<JobRecommendation> getContentBasedRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the last 10 interactions (search, application, like, dislike, comment) sorted by timestamp
        List<UserInteraction> interactions = userInteractionRepository.findTop10ByUserOrderByTimestampDesc(user);
        if (interactions.isEmpty()) {
            logger.warn("No recent interactions found for user: {}", user.getUsername());
            return Collections.emptyList();
        }

        // Determine dynamic threshold based on interaction count
        int interactionCount = interactions.size();
        double threshold = interactionCount < 2 ? SCORE_THRESHOLD_LOW : SCORE_THRESHOLD_HIGH;

        // Build user profile from interactions with weighted tokens
        Map<String, Double> userProfile = buildUserProfile(interactions);

        // Gather applied job IDs to exclude from candidate jobs
        Set<Long> appliedJobIds = interactions.stream()
                .filter(interaction -> interaction.getType() == InteractionType.APPLICATION && interaction.getJob() != null)
                .map(interaction -> interaction.getJob().getId())
                .collect(Collectors.toSet());

        // Fetch candidate jobs (exclude jobs already applied to)
        List<Job> candidateJobs = jobRepository.findAll().stream()
                .filter(job -> !appliedJobIds.contains(job.getId()))
                .collect(Collectors.toList());
        if (candidateJobs.isEmpty()) {
            logger.warn("No candidate jobs available for recommendation.");
            return Collections.emptyList();
        }

        // Compute scores for candidate jobs using Lucene's TF-IDF and fuzzy queries
        Map<Job, Double> jobScores = computeJobScoresWithLucene(userProfile, candidateJobs);

        // EXTRA: Additional repository search to include more jobs
        String combinedSearchQuery = interactions.stream()
                .filter(interaction -> interaction.getType() == InteractionType.SEARCH && interaction.getQuery() != null)
                .map(UserInteraction::getQuery)
                .collect(Collectors.joining(" "));
        if (!combinedSearchQuery.isEmpty()) {
            // Use the repository method to search across all key fields
            List<Job> extraJobs = jobRepository.searchByAllFields(combinedSearchQuery);
            List<String> queryTokens = tokenize(combinedSearchQuery);
            for (Job job : extraJobs) {
                if (!appliedJobIds.contains(job.getId()) && !jobScores.containsKey(job)) {
                    double extraScore = computeExtraJobScore(job, queryTokens);
                    jobScores.put(job, extraScore);
                }
            }
        }

        // Filter and sort recommended jobs using dynamic threshold
        return jobScores.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold) // Dynamic threshold applied here
                .sorted(Map.Entry.<Job, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new JobRecommendation(
                        entry.getKey().getId(),
                        entry.getKey().getTitle(),
                        entry.getKey().getCompany(),
                        entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Builds a user profile from recent interactions.
     */
    private Map<String, Double> buildUserProfile(List<UserInteraction> interactions) {
        Map<String, Double> userProfile = new HashMap<>();
        for (UserInteraction interaction : interactions) {
            Job job = interaction.getJob();
            if (job == null) continue;

            switch (interaction.getType()) {
                case SEARCH:
                    if (interaction.getQuery() != null) {
                        List<String> tokens = tokenize(interaction.getQuery());
                        for (String token : tokens) {
                            userProfile.put(token, userProfile.getOrDefault(token, 0.0) + SEARCH_QUERY_WEIGHT);
                        }
                    }
                    break;

                case APPLICATION:
                    processJobTokens(userProfile, job, APPLIED_JOB_BASE_WEIGHT * APPLIED_JOB_BOOST);
                    break;

                case LIKE:
                    processJobTokens(userProfile, job, LIKE_WEIGHT);
                    break;

                case DISLIKE:
                    processJobTokens(userProfile, job, DISLIKE_WEIGHT);
                    break;

                case COMMENT:
                    processJobTokens(userProfile, job, COMMENT_WEIGHT);
                    if (interaction.getCommentText() != null) {
                        List<String> commentTokens = tokenize(interaction.getCommentText());
                        for (String token : commentTokens) {
                            userProfile.put(token, userProfile.getOrDefault(token, 0.0) + 0.5); // Additional weight for comment text
                        }
                    }
                    break;
            }
        }
        return userProfile;
    }

    /**
     * Processes job tokens and adds them to the user profile with weighted scores.
     */
    private void processJobTokens(Map<String, Double> userProfile, Job job, double weightMultiplier) {
        // Process title tokens
        List<String> titleTokens = tokenize(job.getTitle());
        for (String token : titleTokens) {
            userProfile.put(token, userProfile.getOrDefault(token, 0.0) + TITLE_TOKEN_WEIGHT * weightMultiplier);
        }

        // Process required skills tokens
        if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
            String skillsText = String.join(" ", job.getRequiredSkills());
            List<String> skillsTokens = tokenize(skillsText);
            for (String token : skillsTokens) {
                userProfile.put(token, userProfile.getOrDefault(token, 0.0) + SKILLS_TOKEN_WEIGHT * weightMultiplier);
            }
        }

        // Process description tokens
        List<String> descriptionTokens = tokenize(job.getDescription());
        for (String token : descriptionTokens) {
            userProfile.put(token, userProfile.getOrDefault(token, 0.0) + DESCRIPTION_TOKEN_WEIGHT * weightMultiplier);
        }
    }

    /**
     * Uses an in-memory Lucene index to compute job scores based on the user profile.
     */
    private Map<Job, Double> computeJobScoresWithLucene(Map<String, Double> userProfile, List<Job> candidateJobs) {
        Map<Job, Double> jobScores = new HashMap<>();
        try (Directory directory = new RAMDirectory();
             Analyzer analyzer = new StandardAnalyzer();
             IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {

            // Index each candidate job
            for (Job job : candidateJobs) {
                Document doc = new Document();
                doc.add(new StringField("jobId", String.valueOf(job.getId()), Field.Store.YES));

                String jobContent = String.join(" ",
                        Optional.ofNullable(job.getTitle()).orElse(""),
                        Optional.ofNullable(job.getRequiredSkills()).orElse(Collections.emptyList())
                                .stream().collect(Collectors.joining(" ")),
                        Optional.ofNullable(job.getDescription()).orElse("")
                );
                doc.add(new TextField("content", jobContent, Field.Store.NO));
                doc.add(new TextField("company", Optional.ofNullable(job.getCompany()).orElse(""), Field.Store.NO));
                doc.add(new TextField("location", Optional.ofNullable(job.getLocation()).orElse(""), Field.Store.NO));
                doc.add(new TextField("category", Optional.ofNullable(job.getCategory()).orElse(""), Field.Store.NO));
                writer.addDocument(doc);
            }
            writer.commit();

            // Build Lucene query from user profile tokens
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            for (Map.Entry<String, Double> entry : userProfile.entrySet()) {
                String token = entry.getKey();
                float boost = entry.getValue().floatValue();

                // Exact match queries for content, company, and location
                Term termContent = new Term("content", token);
                Query queryContent = new BoostQuery(new TermQuery(termContent), boost);
                booleanQuery.add(queryContent, BooleanClause.Occur.SHOULD);

                Term termCompany = new Term("company", token);
                Query queryCompany = new BoostQuery(new TermQuery(termCompany), boost * 1.5f); // Increased boost for company
                booleanQuery.add(queryCompany, BooleanClause.Occur.SHOULD);

                Term termLocation = new Term("location", token);
                Query queryLocation = new BoostQuery(new TermQuery(termLocation), boost * 1.2f);
                booleanQuery.add(queryLocation, BooleanClause.Occur.SHOULD);

                // Fuzzy queries for approximate (around 50% match)
                Query fuzzyContent = new BoostQuery(new FuzzyQuery(new Term("content", token)), boost * 0.3f);
                booleanQuery.add(fuzzyContent, BooleanClause.Occur.SHOULD);

                Query fuzzyCompany = new BoostQuery(new FuzzyQuery(new Term("company", token)), boost * 0.3f);
                booleanQuery.add(fuzzyCompany, BooleanClause.Occur.SHOULD);

                Query fuzzyLocation = new BoostQuery(new FuzzyQuery(new Term("location", token)), boost * 0.3f);
                booleanQuery.add(fuzzyLocation, BooleanClause.Occur.SHOULD);
            }
            Query query = booleanQuery.build();

            try (DirectoryReader reader = DirectoryReader.open(directory)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                TopDocs topDocs = searcher.search(query, candidateJobs.size());
                Map<String, Float> docScores = new HashMap<>();
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document doc = searcher.doc(scoreDoc.doc);
                    String jobId = doc.get("jobId");
                    docScores.put(jobId, scoreDoc.score);
                }
                // Map scores back to candidate jobs if above the threshold
                for (Job job : candidateJobs) {
                    Float score = docScores.get(String.valueOf(job.getId()));
                    if (score != null && score > SCORE_THRESHOLD_LOW) { // Use lower threshold for Lucene scores
                        jobScores.put(job, score.doubleValue());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Lucene indexing/search error", e);
        }
        return jobScores;
    }

    /**
     * Computes an extra score for a job based on field-specific matches.
     */
    private double computeExtraJobScore(Job job, List<String> queryTokens) {
        if (queryTokens.isEmpty()) return 0.0;

        double totalContribution = 0.0;
        for (String token : queryTokens) {
            double contribution = 0.0;

            // Check each field with field-specific weights
            contribution += checkFieldMatch(job.getTitle(), token, 1.0);        // Title has highest weight
            contribution += checkFieldMatch(job.getCompany(), token, 0.9);      // Company weight
            contribution += checkFieldMatch(job.getLocation(), token, 0.8);     // Location weight
            contribution += checkFieldMatch(job.getCategory(), token, 0.7);     // Category weight
            contribution += checkFieldMatch(job.getDescription(), token, 0.6);  // Description weight

            // Check required skills
            if (job.getRequiredSkills() != null) {
                contribution += job.getRequiredSkills().stream()
                        .mapToDouble(skill -> checkFieldMatch(skill, token, 0.8)) // Skills weight
                        .sum();
            }

            totalContribution += contribution;
        }

        double avgContribution = totalContribution / queryTokens.size();
        double extraScore = avgContribution * 1.2; // Apply overall boost
        return Math.min(extraScore, 1.2); // Cap score at 1.2
    }

    /**
     * Checks for token presence in a field and returns weighted contribution.
     */
    private double checkFieldMatch(String fieldValue, String token, double weight) {
        if (fieldValue == null) return 0.0;

        String lowerField = fieldValue.toLowerCase();
        String lowerToken = token.toLowerCase();

        if (fieldValue.contains(token)) { // Exact case-sensitive match
            return weight;
        } else if (lowerField.contains(lowerToken)) { // Case-insensitive match
            return weight * 0.9;
        }
        return 0.0;
    }

    /**
     * Tokenizes a string into lowercase tokens.
     */
    private List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * DTO for job recommendations.
     */

}
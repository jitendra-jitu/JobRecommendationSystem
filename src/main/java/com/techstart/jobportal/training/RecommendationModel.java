package com.techstart.jobportal.training;

import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.UserInteraction;
import com.techstart.jobportal.repository.JobRepository;
import com.techstart.jobportal.repository.UserInteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecommendationModel {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserInteractionRepository userInteractionRepository;

    // Data structures to store trained model data
    private Map<Long, Map<Long, Double>> userJobInteractions; // User ID -> (Job ID -> Interaction Weight)
    private Map<Long, Set<String>> jobFeatures; // Job ID -> Set of features (title, skills, etc.)
    private Map<Long, Set<Long>> similarUsers; // User ID -> Set of similar user IDs

    public RecommendationModel() {
        this.userJobInteractions = new HashMap<>();
        this.jobFeatures = new HashMap<>();
        this.similarUsers = new HashMap<>();
    }

    /**
     * Train the recommendation model using user interactions.
     *
     * @param interactions List of user interactions (searches and applications).
     */
    public void train(List<UserInteraction> interactions) {
        System.out.println("Training recommendation model with " + interactions.size() + " interactions...");

        // Step 1: Build user-job interaction matrix
        buildUserJobInteractionMatrix(interactions);

        // Step 2: Extract job features for content-based filtering
        extractJobFeatures();

        // Step 3: Identify similar users for collaborative filtering
        identifySimilarUsers();

        System.out.println("Model training completed.");
    }

    /**
     * Build the user-job interaction matrix.
     *
     * @param interactions List of user interactions.
     */
    private void buildUserJobInteractionMatrix(List<UserInteraction> interactions) {
        for (UserInteraction interaction : interactions) {
            Long userId = interaction.getUser().getId();
            Long jobId = interaction.getJob().getId();
            double weight = interaction.getWeight();

            userJobInteractions.computeIfAbsent(userId, k -> new HashMap<>()).put(jobId, weight);
        }
    }

    /**
     * Extract job features (title, description, skills, etc.) for content-based filtering.
     */
    private void extractJobFeatures() {
        List<Job> jobs = jobRepository.findAll();
        for (Job job : jobs) {
            Set<String> features = new HashSet<>();
            features.add(job.getTitle().toLowerCase());
            features.addAll(Arrays.asList(job.getDescription().toLowerCase().split(" ")));

            // Ensure requiredSkills is a list, process each skill separately
            if (job.getRequiredSkills() != null) {
                job.getRequiredSkills().forEach(skill -> features.add(skill.toLowerCase()));
            }

            jobFeatures.put(job.getId(), features);
        }
    }


    /**
     * Identify similar users based on shared job interactions.
     */
    private void identifySimilarUsers() {
        for (Long userId : userJobInteractions.keySet()) {
            Set<Long> similarUserSet = new HashSet<>();

            // Find users who have interacted with the same jobs
            for (Long otherUserId : userJobInteractions.keySet()) {
                if (!userId.equals(otherUserId)) {
                    Set<Long> commonJobs = new HashSet<>(userJobInteractions.get(userId).keySet());
                    commonJobs.retainAll(userJobInteractions.get(otherUserId).keySet());

                    if (!commonJobs.isEmpty()) {
                        similarUserSet.add(otherUserId);
                    }
                }
            }

            similarUsers.put(userId, similarUserSet);
        }
    }

    /**
     * Predict job recommendations for a user using a hybrid approach.
     *
     * @param userId The ID of the user.
     * @return List of recommended job IDs.
     */
    public List<Long> predict(Long userId) {
        System.out.println("Generating recommendations for user: " + userId);

        // Step 1: Content-based filtering
        List<Long> contentBasedRecommendations = getContentBasedRecommendations(userId);

        // Step 2: Collaborative filtering
        List<Long> collaborativeRecommendations = getCollaborativeRecommendations(userId);

        // Step 3: Combine and rank recommendations
        Map<Long, Double> jobScores = new HashMap<>();

        // Assign weights to each approach
        double contentWeight = 0.6; // Higher weight for content-based
        double collaborativeWeight = 0.4;

        // Score content-based recommendations
        for (Long jobId : contentBasedRecommendations) {
            jobScores.put(jobId, jobScores.getOrDefault(jobId, 0.0) + contentWeight);
        }

        // Score collaborative recommendations
        for (Long jobId : collaborativeRecommendations) {
            jobScores.put(jobId, jobScores.getOrDefault(jobId, 0.0) + collaborativeWeight);
        }

        // Sort jobs by score in descending order
        return jobScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Get content-based recommendations for a user.
     *
     * @param userId The ID of the user.
     * @return List of recommended job IDs.
     */
    private List<Long> getContentBasedRecommendations(Long userId) {
        Set<Long> interactedJobs = userJobInteractions.getOrDefault(userId, new HashMap<>()).keySet();
        Map<Long, Double> jobSimilarityScores = new HashMap<>();

        for (Long jobId : jobFeatures.keySet()) {
            if (!interactedJobs.contains(jobId)) {
                double similarity = calculateJobSimilarity(interactedJobs, jobId);
                jobSimilarityScores.put(jobId, similarity);
            }
        }

        return jobSimilarityScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate similarity between a job and the user's interacted jobs.
     *
     * @param interactedJobs Set of job IDs the user has interacted with.
     * @param targetJobId    The job ID to compare.
     * @return Similarity score.
     */
    private double calculateJobSimilarity(Set<Long> interactedJobs, Long targetJobId) {
        Set<String> targetFeatures = jobFeatures.get(targetJobId);
        double maxSimilarity = 0.0;

        for (Long jobId : interactedJobs) {
            Set<String> features = jobFeatures.get(jobId);
            double similarity = jaccardSimilarity(targetFeatures, features);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
            }
        }

        return maxSimilarity;
    }

    /**
     * Calculate Jaccard similarity between two sets of features.
     *
     * @param set1 First set of features.
     * @param set2 Second set of features.
     * @return Jaccard similarity score.
     */
    private double jaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * Get collaborative filtering recommendations for a user.
     *
     * @param userId The ID of the user.
     * @return List of recommended job IDs.
     */
    private List<Long> getCollaborativeRecommendations(Long userId) {
        Set<Long> recommendations = new HashSet<>();

        for (Long similarUserId : similarUsers.getOrDefault(userId, new HashSet<>())) {
            recommendations.addAll(userJobInteractions.get(similarUserId).keySet());
        }

        return new ArrayList<>(recommendations);
    }
}
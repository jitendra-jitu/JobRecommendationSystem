package com.techstart.jobportal.recommendation.collabBaesd;

import com.techstart.jobportal.dto.JobRecommendation;
import com.techstart.jobportal.model.*;
import com.techstart.jobportal.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/recommendations")
public class CollaborativeFilteringController {

    private static final Logger logger = LoggerFactory.getLogger(CollaborativeFilteringController.class);

    // Weights for different types of interactions
    private static final double LIKE_WEIGHT = 1.0;
    private static final double DISLIKE_WEIGHT = -1.0;
    private static final double COMMENT_WEIGHT = 0.8;
    private static final double APPLICATION_WEIGHT = 1.5;
    private static final double IGNORED_JOB_WEIGHT = -0.5;

    // Thresholds for similarity and recommendations
    private static final double USER_SIMILARITY_THRESHOLD = 0.7;
    private static final double JOB_SCORE_THRESHOLD = 0.5;

    private final UserRepository userRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final JobRepository jobRepository;

    public CollaborativeFilteringController(UserRepository userRepository,
                                            UserInteractionRepository userInteractionRepository,
                                            JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.userInteractionRepository = userInteractionRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/collaborative")
    public List<JobRecommendation> getCollaborativeRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 1: Fetch all users and their interactions
        List<User> allUsers = userRepository.findAll();
        Map<User, Map<Job, Double>> userJobInteractions = new HashMap<>();
        for (User user : allUsers) {
            Map<Job, Double> jobScores = getUserJobInteractionScores(user);
            userJobInteractions.put(user, jobScores);
        }

        // Step 2: Find similar users based on interaction patterns
        List<User> similarUsers = findSimilarUsers(currentUser, userJobInteractions, USER_SIMILARITY_THRESHOLD);
        if (similarUsers.isEmpty()) {
            logger.warn("No similar users found for: {}", currentUser.getUsername());
            return Collections.emptyList();
        }

        // Step 3: Recommend jobs based on similar users' interactions
        Map<Job, Double> recommendedJobs = recommendJobsFromSimilarUsers(currentUser, similarUsers, userJobInteractions);

        // Step 4: Filter and sort recommended jobs
        return recommendedJobs.entrySet().stream()
                .filter(entry -> entry.getValue() > JOB_SCORE_THRESHOLD)
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
     * Computes interaction scores for a user based on their interactions.
     */
    private Map<Job, Double> getUserJobInteractionScores(User user) {
        Map<Job, Double> jobScores = new HashMap<>();
        List<UserInteraction> interactions = userInteractionRepository.findByUser(user);

        for (UserInteraction interaction : interactions) {
            Job job = interaction.getJob();
            if (job == null) continue;

            double score = 0.0;
            switch (interaction.getType()) {
                case LIKE:
                    score = LIKE_WEIGHT;
                    break;
                case DISLIKE:
                    score = DISLIKE_WEIGHT;
                    break;
                case COMMENT:
                    score = COMMENT_WEIGHT;
                    break;
                case APPLICATION:
                    score = APPLICATION_WEIGHT;
                    break;
                case SEARCH:
                    // Search interactions are handled in content-based recommendations
                    continue;
            }
            jobScores.put(job, jobScores.getOrDefault(job, 0.0) + score);
        }

        return jobScores;
    }

    /**
     * Finds users similar to the current user based on interaction patterns.
     */
    private List<User> findSimilarUsers(User currentUser, Map<User, Map<Job, Double>> userJobInteractions, double threshold) {
        Map<Job, Double> currentUserInteractions = userJobInteractions.get(currentUser);
        List<User> similarUsers = new ArrayList<>();

        for (User user : userJobInteractions.keySet()) {
            if (user.equals(currentUser)) continue; // Skip the current user

            Map<Job, Double> otherUserInteractions = userJobInteractions.get(user);
            double similarity = computeUserSimilarity(currentUserInteractions, otherUserInteractions);

            if (similarity > threshold) {
                similarUsers.add(user);
            }
        }

        return similarUsers;
    }

    /**
     * Computes similarity between two users using cosine similarity.
     */
    private double computeUserSimilarity(Map<Job, Double> user1Interactions, Map<Job, Double> user2Interactions) {
        Set<Job> commonJobs = new HashSet<>(user1Interactions.keySet());
        commonJobs.retainAll(user2Interactions.keySet());

        if (commonJobs.isEmpty()) return 0.0;

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Job job : commonJobs) {
            double score1 = user1Interactions.get(job);
            double score2 = user2Interactions.get(job);
            dotProduct += score1 * score2;
            norm1 += score1 * score1;
            norm2 += score2 * score2;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Recommends jobs based on similar users' interactions.
     */
    private Map<Job, Double> recommendJobsFromSimilarUsers(User currentUser, List<User> similarUsers, Map<User, Map<Job, Double>> userJobInteractions) {
        Map<Job, Double> recommendedJobs = new HashMap<>();
        Set<Job> currentUserJobs = userJobInteractions.get(currentUser).keySet();

        for (User similarUser : similarUsers) {
            Map<Job, Double> similarUserInteractions = userJobInteractions.get(similarUser);

            for (Map.Entry<Job, Double> entry : similarUserInteractions.entrySet()) {
                Job job = entry.getKey();
                double score = entry.getValue();

                // Skip jobs the current user has already interacted with
                if (currentUserJobs.contains(job)) continue;

                recommendedJobs.put(job, recommendedJobs.getOrDefault(job, 0.0) + score);
            }
        }

        return recommendedJobs;
    }

}
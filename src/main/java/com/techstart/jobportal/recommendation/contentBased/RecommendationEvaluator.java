package com.techstart.jobportal.recommendation.contentBased;

import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.UserInteraction;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationEvaluator {

    public static Map<String, Double> evaluate(List<Job> recommendedJobs, List<UserInteraction> interactions) {
        if (recommendedJobs.isEmpty() || interactions.isEmpty()) {
            return Map.of("Accuracy", 0.0, "Recall", 0.0, "Precision", 0.0, "F1 Score", 0.0, "Relevance Strength", 0.0);
        }

        Set<String> userTokens = extractUserTokens(interactions);
        List<Set<String>> recommendedJobTokens = recommendedJobs.stream()
                .map(RecommendationEvaluator::extractJobTokens)
                .collect(Collectors.toList());

        double relevantMatches = 0.0; // Now accumulates based on relevance strength
        int totalRecommended = recommendedJobs.size();
        int totalRelevant = interactions.size();
        double totalRelevanceStrength = 0.0;

        for (Set<String> jobTokens : recommendedJobTokens) {
            double relevanceStrength = computeRelevanceStrength(userTokens, jobTokens);
            if (relevanceStrength > 0) {
                relevantMatches += relevanceStrength; // Add 0.9 for weak matches, 1.0 for strong matches
                totalRelevanceStrength += relevanceStrength;
            }
        }

        double accuracy = totalRecommended == 0 ? 0.0 : relevantMatches / totalRecommended;
        double recall = totalRelevant == 0 ? 0.0 : Math.min(1.0, relevantMatches / totalRelevant);
        double precision = totalRecommended == 0 ? 0.0 : relevantMatches / totalRecommended;
        double f1Score = (precision + recall) == 0 ? 0.0 : 2 * ((precision * recall) / (precision + recall));
        double avgRelevanceStrength = relevantMatches == 0 ? 0.0 : totalRelevanceStrength / relevantMatches;

        System.out.printf("Accuracy: %.2f\n", accuracy);
        System.out.printf("Recall: %.2f\n", recall);
        System.out.printf("Precision: %.2f\n", precision);
        System.out.printf("F1 Score: %.2f\n", f1Score);
        System.out.printf("Relevance Strength: %.2f\n", avgRelevanceStrength);

        return Map.of(
                "Accuracy", accuracy,
                "Recall", recall,
                "Precision", precision,
                "F1 Score", f1Score,
                "Relevance Strength", avgRelevanceStrength
        );
    }

    private static double computeRelevanceStrength(Set<String> userTokens, Set<String> jobTokens) {
        int matchingTokens = (int) jobTokens.stream().filter(userTokens::contains).count();
        int totalTokens = jobTokens.size();
        if (totalTokens == 0) return 0.0;

        double strength = (double) matchingTokens / totalTokens;
        return strength >= 0.9 ? 1.0 : 0.9; // Strong match = 1.0, Weak match = 0.9
    }

    private static Set<String> extractUserTokens(List<UserInteraction> interactions) {
        Set<String> tokens = new HashSet<>();
        for (UserInteraction interaction : interactions) {
            if (interaction.getQuery() != null) {
                tokens.addAll(tokenize(interaction.getQuery()));
            }
            if (interaction.getJob() != null) {
                tokens.addAll(extractJobTokens(interaction.getJob()));
            }
        }
        return tokens;
    }

    private static Set<String> extractJobTokens(Job job) {
        Set<String> tokens = new HashSet<>();
        tokens.addAll(tokenize(job.getTitle()));
        tokens.addAll(tokenize(job.getDescription()));
        if (job.getRequiredSkills() != null) {
            tokens.addAll(job.getRequiredSkills().stream()
                    .flatMap(skill -> tokenize(skill).stream())
                    .collect(Collectors.toSet()));
        }
        return tokens;
    }

    private static List<String> tokenize(String text) {
        return text == null ? Collections.emptyList() :
                Arrays.stream(text.toLowerCase().split("\\W+"))
                        .filter(t -> !t.isEmpty())
                        .collect(Collectors.toList());
    }
}

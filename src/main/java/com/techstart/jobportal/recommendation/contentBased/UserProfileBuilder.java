// UserProfileBuilder.java
package com.techstart.jobportal.recommendation.contentBased;

import com.techstart.jobportal.model.InteractionType;
import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.UserInteraction;

import java.util.*;
import java.util.stream.Collectors;

public class UserProfileBuilder {
    private static final double SEARCH_QUERY_WEIGHT = 1.1;
    private static final double APPLIED_JOB_BASE_WEIGHT = 0.5;
    private static final double APPLIED_JOB_BOOST = 1.0;
    private static final double TITLE_TOKEN_WEIGHT = 1.0;
    private static final double SKILLS_TOKEN_WEIGHT = 0.5;
    private static final double DESCRIPTION_TOKEN_WEIGHT = 0.4;

    public static Map<String, Double> buildUserProfile(List<UserInteraction> interactions) {
        Map<String, Double> userProfile = new HashMap<>();
        for (UserInteraction interaction : interactions) {
            if (interaction.getType() == InteractionType.SEARCH) {
                List<String> tokens = tokenize(interaction.getQuery());
                for (String token : tokens) {
                    userProfile.put(token, userProfile.getOrDefault(token, 0.0) + SEARCH_QUERY_WEIGHT);
                }
            } else if (interaction.getType() == InteractionType.APPLICATION && interaction.getJob() != null) {
                Job job = interaction.getJob();
                processJobTokens(job.getTitle(), TITLE_TOKEN_WEIGHT, userProfile);
                processJobTokens(job.getDescription(), DESCRIPTION_TOKEN_WEIGHT, userProfile);
                if (job.getRequiredSkills() != null) {
                    processJobTokens(String.join(" ", job.getRequiredSkills()), SKILLS_TOKEN_WEIGHT, userProfile);
                }
            }
        }
        return userProfile;
    }

    private static void processJobTokens(String text, double weight, Map<String, Double> profile) {
        for (String token : tokenize(text)) {
            profile.put(token, profile.getOrDefault(token, 0.0) + weight * APPLIED_JOB_BASE_WEIGHT * APPLIED_JOB_BOOST);
        }
    }

    private static List<String> tokenize(String text) {
        return text == null ? Collections.emptyList() : Arrays.stream(text.toLowerCase().split("\\W+")).filter(t -> !t.isEmpty()).collect(Collectors.toList());
    }
}
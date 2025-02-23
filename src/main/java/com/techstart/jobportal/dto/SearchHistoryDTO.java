package com.techstart.jobportal.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SearchHistoryDTO {
    private Long id;
    private String query;
    private LocalDateTime timestamp;

    // Getters and setters

    @Data
    public static class JobRecommendation {
        private Long jobId;
        private String title;
        private String company;
        private double relevanceScore;

        public JobRecommendation(Long jobId, String title, String company, double relevanceScore) {
            this.jobId = jobId;
            this.title = title;
            this.company = company;
            this.relevanceScore = relevanceScore;
        }
    }
}
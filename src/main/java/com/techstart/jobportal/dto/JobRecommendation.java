

// JobRecommendation DTO
package com.techstart.jobportal.dto;

import lombok.Data;

@Data
public class JobRecommendation {
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
package com.techstart.jobportal.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String author;
    private long date;


    public Comment(String author, String text, long date) {

        this.author = author;
        this.text = text;
        this.date = date;
    }


    @Data
    public static class RecommendationDTO {
        private Long jobId;
        private String title;
        private String description;
        private double relevanceScore;
    }
}

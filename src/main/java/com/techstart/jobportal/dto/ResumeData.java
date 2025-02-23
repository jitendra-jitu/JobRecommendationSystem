package com.techstart.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeData {

    private String fullName;
    private String email;
    private List<String> skills;
    private List<Experience> experience;
    private List<Education> education;
    private String careerGoals;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Experience {
        private String jobTitle;
        private String company;
        private String duration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
        private String degree;
        private String institution;
        private String year;
    }
}

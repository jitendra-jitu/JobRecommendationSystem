package com.techstart.jobportal.dto;

import lombok.Data;
import java.util.List;

@Data
public class JobDTO {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String type;
    private String category;
    private String description;
    private long postedDate;
    private long Salary;
    private List<String> requiredSkills;
    private ExperienceRequiredDTO experienceRequired;
    private List<CommentDTO> comments;

    private int likeCount;
}

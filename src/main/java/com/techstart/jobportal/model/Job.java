package com.techstart.jobportal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;
    private String type;
    private String category;
    private String description;
    private long postedDate;



    private long Salary;

    @ElementCollection
    private List<String> requiredSkills;


    @OneToOne(cascade = CascadeType.ALL)
    private ExperienceRequired experienceRequired;



    // In Job.java, update the comments relationship:
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;


    private int likeCount;

    // Manually define the constructor
    public Job(String title, String company, String location, String type, String category, String description, long postedDate, long Salary, List<String> requiredSkills, ExperienceRequired experienceRequired, List<Comment> comments, int likeCount) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.type = type;
        this.category = category;
        this.description = description;
        this.postedDate = postedDate;
        this.Salary = Salary;
        this.requiredSkills = requiredSkills;
        this.experienceRequired = experienceRequired;
        this.comments = comments;
        this.likeCount = likeCount;
    }
}

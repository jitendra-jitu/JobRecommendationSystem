package com.techstart.jobportal.controller;

import com.techstart.jobportal.model.Comment;
import com.techstart.jobportal.model.*;
import com.techstart.jobportal.model.User;
import com.techstart.jobportal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.techstart.jobportal.repository.*;


@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserInteractionRepository userInteractionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/alljobs")
    public ResponseEntity<List<Job>> initializeJobs() {
        return ResponseEntity.ok(jobService.checkAndInsertDefaultJobs());
    }

    @PostMapping("/jobs/{jobId}/like")
    public ResponseEntity<Void> likeJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobService.findById(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setJob(job);
        interaction.setType(InteractionType.LIKE);
        interaction.setTimestamp(LocalDateTime.now());
        interaction.setWeight(1.0); // Set weight for LIKE interaction
        userInteractionRepository.save(interaction);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/jobs/{jobId}/dislike")
    public ResponseEntity<Void> dislikeJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobService.findById(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setJob(job);
        interaction.setType(InteractionType.DISLIKE);
        interaction.setTimestamp(LocalDateTime.now());
        interaction.setWeight(-1.0); // Set weight for DISLIKE interaction
        userInteractionRepository.save(interaction);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/jobs/{jobId}/comment")
    public ResponseEntity<Void> addComment(@PathVariable Long jobId,
                                           @RequestBody Comment comment,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        // Lookup the user by username
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lookup the job by jobId
        Job job = jobService.findById(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        // Create a new user interaction for the COMMENT
        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setJob(job);
        interaction.setType(InteractionType.COMMENT);
        interaction.setCommentText(comment.getText());
        interaction.setTimestamp(LocalDateTime.now());
        interaction.setWeight(0.8); // Set weight for COMMENT interaction

        // Save the user interaction
        userInteractionRepository.save(interaction);

        // **Add the comment to the job's comment list**
        // Make sure the job's comment list is initialized (not null)
        if (job.getComments() == null) {
            job.setComments(new ArrayList<>());
        }
        job.getComments().add(comment);

        // Save the updated job
        jobService.save(job);  // Ensure your JobService/repository supports saving/updating Job

        return ResponseEntity.ok().build();
    }

}
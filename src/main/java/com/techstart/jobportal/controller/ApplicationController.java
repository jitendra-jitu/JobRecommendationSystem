package com.techstart.jobportal.controller;

import com.techstart.jobportal.dto.AppliedJobDTO;
import com.techstart.jobportal.model.*;
import com.techstart.jobportal.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jobs")
public class ApplicationController {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final UserRepository userRepository;

    public ApplicationController(
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            UserInteractionRepository userInteractionRepository,
            UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.userInteractionRepository = userInteractionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{jobId}/apply")
    public Application applyToJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Fetch managed user entity
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        // Save application
        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setApplicationDate(new Date());
        applicationRepository.save(application);

        // Log application interaction
        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setJob(job);
        interaction.setType(InteractionType.APPLICATION);
        interaction.setTimestamp(LocalDateTime.now());
//        interaction.setWeight(1.0);
        userInteractionRepository.save(interaction);

        return application;
    }

    @GetMapping("/applied")
    public List<AppliedJobDTO> getAppliedJobs(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Application> applications = applicationRepository.findByUser(user);

        return applications.stream()
                .map(app -> new AppliedJobDTO(app.getJob(), app.getApplicationDate()))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{jobId}/withdraw")
    @Transactional
    public ResponseEntity<String> withdrawApplication(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete application
        Application application = applicationRepository.findByUserAndJobId(user, jobId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        applicationRepository.delete(application);

        // Delete associated interaction
        userInteractionRepository.deleteByUserAndJobAndType(user, application.getJob(), InteractionType.APPLICATION);

        return ResponseEntity.ok("Application and associated interaction withdrawn successfully");
    }
}
package com.techstart.jobportal.controller;

import com.techstart.jobportal.model.*;
import com.techstart.jobportal.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
public class JobSearchController {

    private final JobRepository jobRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final UserInteractionRepository userInteractionRepository;

    public JobSearchController(
            JobRepository jobRepository,
            SearchHistoryRepository searchHistoryRepository,
            UserRepository userRepository,
            UserInteractionRepository userInteractionRepository) {
        this.jobRepository = jobRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.userRepository = userRepository;
        this.userInteractionRepository = userInteractionRepository;
    }

    @GetMapping("/search")
    public List<Job> searchJobs(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Fetch the full User entity from DB
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found in database");
        }

        User user = userOptional.get();

        // Save search history
        SearchHistory history = new SearchHistory();
        history.setQuery(query);
        history.setUser(user);
        history.setTimestamp(LocalDateTime.now());
        searchHistoryRepository.save(history);

        // Log search interaction
        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setType(InteractionType.SEARCH);
        interaction.setTimestamp(LocalDateTime.now());
//        interaction.setWeight(0.5);
        interaction.setQuery(query);
        userInteractionRepository.save(interaction);

        // Search jobs
        return jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(
                query, query, query, query, query);
    }

    @GetMapping("/history")
    public List<SearchHistory> getUserSearchHistory(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found in database");
        }

        return searchHistoryRepository.findByUser(userOptional.get());
    }

    @DeleteMapping("/history/clear")
    @Transactional
    public ResponseEntity<String> clearSearchHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String range) {

        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        LocalDateTime cutoffTime = calculateCutoffTime(range);

        if (cutoffTime != null) {
            // Delete search history and associated interactions
            searchHistoryRepository.deleteByUserAndTimestampAfter(user, cutoffTime);
            userInteractionRepository.deleteByUserAndTypeAndTimestampAfter(user, InteractionType.SEARCH, cutoffTime);
        } else {
            // Delete all search history and associated interactions
            searchHistoryRepository.deleteByUser(user);
            userInteractionRepository.deleteByUserAndType(user, InteractionType.SEARCH);
        }

        return ResponseEntity.ok("Search history and associated interactions cleared successfully");
    }

    private LocalDateTime calculateCutoffTime(String range) {
        LocalDateTime now = LocalDateTime.now();
        return switch (range.toLowerCase()) {
            case "last_hour" -> now.minusHours(1);
            case "last_day" -> now.minusDays(1);
            case "last_week" -> now.minusWeeks(1);
            case "last_month" -> now.minusMonths(1);
            case "all" -> null; // Delete all history
            default -> throw new IllegalArgumentException("Invalid range: " + range);
        };
    }
}
package com.techstart.jobportal.controller;

import com.techstart.jobportal.model.*;
import com.techstart.jobportal.repository.UserInteractionRepository;
import com.techstart.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user-interactions")
public class UserInteractionController {

    @Autowired
    private UserInteractionRepository userInteractionRepository;

    @Autowired
    private UserRepository userRepository;


    // Reset interactions for the logged-in user
    @DeleteMapping("/reset")
    public String resetUserInteractions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userInteractionRepository.deleteByUser(user);
        return "User interactions reset successfully.";
    }

    // Reset interactions for all users (admin only)
    @DeleteMapping("/reset-all")
    public String resetAllInteractions() {
        userInteractionRepository.deleteAll();
        return "All user interactions have been reset.";
    }
}

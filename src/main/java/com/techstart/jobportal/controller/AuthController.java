package com.techstart.jobportal.controller;

import com.techstart.jobportal.dto.AuthenticationRequest;
import com.techstart.jobportal.dto.AuthenticationResponse;
import com.techstart.jobportal.jwt.JwtService;
import com.techstart.jobportal.model.User;

import com.techstart.jobportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//@CrossOrigin(origins = {
//        "https://0971c8cb-d3f8-40e1-956c-84d60cc7a2dc.lovableproject.com",
//        "https://id-preview--0971c8cb-d3f8-40e1-956c-84d60cc7a2dc.lovable.app"
//})
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            String token = jwtService.generateToken(request.getUsername());
            return new AuthenticationResponse(token);
        }

        throw new RuntimeException("Invalid credentials");
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthenticationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "User registered successfully";
    }
}

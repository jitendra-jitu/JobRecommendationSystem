package com.techstart.jobportal.config;


import com.techstart.jobportal.model.User;
import com.techstart.jobportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) { // Check if users table is empty
            for (int i = 1; i <= 10; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPassword(passwordEncoder.encode("pass" + i)); // Encode password ("pass1" to "pass10")
                user.setRole("ROLE_USER");
                userRepository.save(user);
            }
            System.out.println("10 users have been preloaded.");
        }
//        else {
////            System.out.println("Users already exist. No preloading needed.");
//        }
    }
}


package com.example.Service;

import com.example.Model.User;
import com.example.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        String normalizedEmail = user.getEmail().toLowerCase();
        Optional<User> existingUser = userRepo.findByEmail(normalizedEmail);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setEmail(normalizedEmail);

        System.out.println("Raw password: " + user.getPassword());
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Encoded password (register): " + encodedPassword);

        user.setPassword(encodedPassword);

        if (user.getRole() == null) {
            user.setRole(User.UserRole.FARMER);
        }

        return userRepo.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepo.findByEmail(email.toLowerCase());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("No user found with this email");
        }

        User user = userOpt.get();
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password from DB: " + user.getPassword());

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

}

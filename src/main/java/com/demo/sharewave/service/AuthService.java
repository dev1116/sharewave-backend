package com.demo.sharewave.service;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.demo.sharewave.entity.User;
import com.demo.sharewave.exception.NotFoundException;
import com.demo.sharewave.exception.UnauthorizedException;
import com.demo.sharewave.object.LoginRequest;
import com.demo.sharewave.object.RegisterRequest;
import com.demo.sharewave.repository.UserRepository;
import com.demo.sharewave.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, String> register(RegisterRequest req) {

        // Validations
        if (req.getUsername() == null || req.getUsername().isBlank())
            throw new RuntimeException("Username required!");
        if (req.getUsername().length() < 3)
            throw new RuntimeException("Username must be at least 3 characters!");
        if (req.getEmail() == null || !req.getEmail().contains("@"))
            throw new RuntimeException("Valid email required!");
        if (req.getPassword() == null || req.getPassword().length() < 6)
            throw new RuntimeException("Password must be at least 6 characters!");
        if (!req.getPassword().equals(req.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match!");

        // Duplicate check
        if (userRepo.findByEmail(req.getEmail()).isPresent())
            throw new RuntimeException("Email already registered!");
        if (userRepo.findByUsername(req.getUsername()).isPresent())
            throw new RuntimeException("Username already taken!");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return Map.of(
            "token", token,
            "username", user.getUsername(),
            "plan", user.getPlan().toString(),
            "storageLimit", user.getStorageLimit().toString(),
            "storageUsed", user.getStorageUsed().toString()
        );
    }

    public Map<String, String> login(LoginRequest req) {

        // Validations
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new RuntimeException("Email required!");
        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new RuntimeException("Password required!");

        // User dhundo
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException(
                    "No account found with this email!"));

        // Account active hai?
        if (user.getStatus() != User.Status.ACTIVE)
            throw new UnauthorizedException("Account suspended!");

        // Password check
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new UnauthorizedException("Wrong password!");

        // Last login update
        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return Map.of(
            "token", token,
            "username", user.getUsername(),
            "plan", user.getPlan().toString(),
            "storageLimit", user.getStorageLimit().toString(),
            "storageUsed", user.getStorageUsed().toString()
        );
    }
}
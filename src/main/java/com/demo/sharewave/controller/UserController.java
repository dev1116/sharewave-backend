package com.demo.sharewave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.demo.sharewave.object.LoginRequest;
import com.demo.sharewave.object.RegisterRequest;
import com.demo.sharewave.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest req) {

        try {

            return userService.registerUser(req);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body("Something went wrong: " + e.getMessage());
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req) {

        try {

            return userService.authenticateUser(req);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body("Something went wrong: " + e.getMessage());
        }
    }

    @GetMapping("/health-check")
    public String healthCheck() {

        return "Service is running...";
    }
    
    @GetMapping("/test")
    public String test() {
        return "TEST OK";
    }
}
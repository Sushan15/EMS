package com.pelatro.signup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.pelatro.signup.entity.User;
import com.pelatro.signup.payload.LoginRequest;
import com.pelatro.signup.payload.SignupRequest;
import com.pelatro.signup.repository.UserRepository;
import com.pelatro.signup.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
@Service
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Error: Email is already in use!"));
        }

        if (!request.getPassword().equals(request.getReconfirmPassword())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Error: Password and Reconfirm Password do not match!"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody LoginRequest request, HttpServletResponse response) {
        // Fetch user from the database using the username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        // Check if the password matches the one stored in the database
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Error: Incorrect password.");
        }

        // Generate the JWT token for the user
        String jwtToken = jwtUtils.generateJwtToken(user.getUsername());

        // Set the Authorization header in the response
        response.setHeader("Authorization", "Bearer " + jwtToken);

        // Return a structured JSON response with message and token
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "User logged in successfully.");
        responseMap.put("token", jwtToken); // Include the JWT token in the response

        return ResponseEntity.ok(responseMap); // Return response as JSON
    }


    @PostMapping("/signout")
    public String logoutUser(HttpServletResponse response) {
        response.setHeader("Authorization", "");
        return "User logged out successfully!";
    }
}

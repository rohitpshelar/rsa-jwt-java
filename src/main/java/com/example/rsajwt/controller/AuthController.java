package com.example.rsajwt.controller;

import com.example.rsajwt.model.AuthRequest;
import com.example.rsajwt.model.AuthResponse;
import com.example.rsajwt.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    
    // Demo users (in production, use a database with proper user repository)
    // This is intentionally simplified for demonstration purposes
    private static final Map<String, String> DEMO_USERS = new HashMap<>();
    
    public AuthController(JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        
        // Initialize demo users
        DEMO_USERS.put("admin", passwordEncoder.encode("admin123"));
        DEMO_USERS.put("user", passwordEncoder.encode("user123"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }
        
        // Validate user credentials
        String storedPassword = DEMO_USERS.get(username);
        if (storedPassword == null || !passwordEncoder.matches(password, storedPassword)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(username);
        
        return ResponseEntity.ok(new AuthResponse(token, username));
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = jwtService.validateToken(token);
        
        if (isValid) {
            String username = jwtService.extractUsername(token);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username
            ));
        } else {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}

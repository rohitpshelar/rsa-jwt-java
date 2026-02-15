package com.example.rsajwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Sample controller with protected endpoints
 */
@RestController
@RequestMapping("/api")
public class SampleController {
    
    @GetMapping("/public/hello")
    public ResponseEntity<?> publicHello() {
        return ResponseEntity.ok(Map.of(
            "message", "Hello from public endpoint!",
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/protected/hello")
    public ResponseEntity<?> protectedHello(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "Anonymous";
        return ResponseEntity.ok(Map.of(
            "message", "Hello from protected endpoint!",
            "username", username,
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/protected/user")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        return ResponseEntity.ok(Map.of(
            "username", authentication.getName(),
            "authenticated", authentication.isAuthenticated(),
            "authorities", authentication.getAuthorities()
        ));
    }
}

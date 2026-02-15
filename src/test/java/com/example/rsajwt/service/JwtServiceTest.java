package com.example.rsajwt.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService
 */
class JwtServiceTest {
    
    private JwtService jwtService;
    
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }
    
    @Test
    void testGenerateToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
    
    @Test
    void testGenerateTokenWithClaims() {
        String username = "testuser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        claims.put("department", "IT");
        
        String token = jwtService.generateToken(username, claims);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        Claims extractedClaims = jwtService.extractClaims(token);
        assertEquals("admin", extractedClaims.get("role"));
        assertEquals("IT", extractedClaims.get("department"));
    }
    
    @Test
    void testValidateToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        boolean isValid = jwtService.validateToken(token);
        
        assertTrue(isValid);
    }
    
    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtService.validateToken(invalidToken);
        
        assertFalse(isValid);
    }
    
    @Test
    void testExtractUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        String extractedUsername = jwtService.extractUsername(token);
        
        assertEquals(username, extractedUsername);
    }
    
    @Test
    void testTokenNotExpired() {
        String username = "testuser";
        String token = jwtService.generateToken(username);
        
        boolean isExpired = jwtService.isTokenExpired(token);
        
        assertFalse(isExpired);
    }
    
    @Test
    void testKeyPairGeneration() {
        assertNotNull(jwtService.getPublicKey());
        assertNotNull(jwtService.getPrivateKey());
    }
}

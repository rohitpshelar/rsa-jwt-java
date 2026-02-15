package com.example.rsajwt.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for JWT token generation and validation using RSA
 */
@Service
public class JwtService {
    
    private final KeyPair keyPair;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    
    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    
    public JwtService() {
        // Generate RSA key pair
        this.keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
    
    /**
     * Generate JWT token with RSA signature
     */
    public String generateToken(String username) {
        return generateToken(username, new HashMap<>());
    }
    
    /**
     * Generate JWT token with RSA signature and custom claims
     */
    public String generateToken(String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    
    /**
     * Extract all claims from JWT token
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}

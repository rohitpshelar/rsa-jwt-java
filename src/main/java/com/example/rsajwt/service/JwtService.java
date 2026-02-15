package com.example.rsajwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
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
    
    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
        // Generate RSA key pair
        // NOTE: In production, keys should be persisted and loaded from secure storage
        // to ensure tokens remain valid across application restarts.
        // Consider implementing key rotation strategy for enhanced security.
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
     *
     * {
     *   "token": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3MTE0MzQ4MiwiZXhwIjoxNzcxMjI5ODgyfQ.XxtMwGw63V5m_E14b1q0Z_47ENxfat8k9rvcEjddFr4rYjCMthubaxT-J-3Az2f6vUdKDRmr7OsO66amvmXr7RU3qqEoO8Xfix1UwcOEowE-LYX_y5ZsQCLggSKZg_3hr1HwlQESQX5z_FGJqGgpGdZguT0iSWXkY8OsrfY0ztNlM9ykM-uxG_Q6O1IG0JWOuC0eJrq3zgAdiecSQN1ouyc6YfJtnEAV85hJntX4TrHBeXyOQ50Mr7yPZqfUMOBnIrTEEgBMo3AHhbyp3H1maNBgItMfjEY9Q_UIOeyDACsB45QMhcc1xYXgfg_nRVuHzj5WiiEDOmv57TGDixFwVA",
     *   "username": "admin"
     * }
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

    private final JwtEncoder jwtEncoder;

    /**
     * Generate JWT token with RSA signature
     * https://www.youtube.com/watch?v=1-Bf7nrLSds
     */
    public String generateToken(@NonNull UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("Rohit")
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofMinutes(15)))
                .build();

        JwsHeader header = JwsHeader.with(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
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

package com.example.rsajwt.controller;

import com.example.rsajwt.model.AuthRequest;
import com.example.rsajwt.model.AuthResponse;
import com.example.rsajwt.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthControllerRSA {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthControllerRSA(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login( @RequestBody AuthRequest login) {
        // Authenticate the user
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                login.getUsername(),
                                login.getPassword()
                        )
                );
        // Generate the token for the authenticated user
        String token = jwtService.generateToken((UserDetails) auth.getPrincipal());
        // Return the token as response
        return new AuthResponse(token);
    }
}
package com.example.rsajwt.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // For demonstration, we return a hardcoded user. In a real application, you would fetch this from a database.
        if ("admin".equals(username)) {
            String password = "admin123";
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            return new org.springframework.security.core.userdetails.User(
                    "admin",
//                    Encoded password does not look like BCrypt

                    hashedPassword, // {noop} means no password encoding

                    List.of(new SimpleGrantedAuthority("USER"))
            );
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
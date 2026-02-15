package com.example.rsajwt.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Security configuration for JWT authentication
 *
 * SETUP
 * 1. open gitbash terminal and navigate to the project directory
 * 2. command to create private key "openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048"
 * 3. command to create public key "openssl rsa -pubout -in private.pem -out public.pem"
 * 4. copy the generated private.pem and public.pem files to src/main/resources/certs
 * 5. in application.properties, set the paths to the keys:
 * security.jwt.private-key=classpath:private.pem
 * security.jwt.public-key=classpath:public.pem
 */
@Configuration
//@EnableWebSecurity
public class SecurityConfigRSA {

    @Value("${security.jwt.private-key}")
    private RSAPrivateKey privateKey;

    @Value("${security.jwt.public-key}")
    private RSAPublicKey publicKey;

    
    @Bean
  public JwtDecoder jwtDecoder() {
      return NimbusJwtDecoder.withPublicKey(publicKey).build();
  }

  @Bean
    public JwtEncoder jwtEncoder() {
      RSAKey rsa = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
      JWKSource<SecurityContext> jwtks = new ImmutableJWKSet<>(new JWKSet(rsa));
        return new NimbusJwtEncoder(jwtks);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(
//                        auth-> auth
//                                .requestMatchers("/api/v1/login").permitAll()
//                                .anyRequest().authenticated()
//                ).oauth2ResourceServer(
//                        rs -> rs.jwt(Customizer.withDefaults())
//                ).sessionManagement(
//                        session -> session
//                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .build();
//    }
}

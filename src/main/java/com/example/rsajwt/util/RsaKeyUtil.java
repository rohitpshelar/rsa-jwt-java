package com.example.rsajwt.util;

import java.security.*;
import java.util.Base64;

/**
 * Utility class for RSA key pair generation and management
 */
public class RsaKeyUtil {
    
    private static final int KEY_SIZE = 2048;
    
    /**
     * Generate a new RSA key pair
     */
    public static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }
    
    /**
     * Convert public key to Base64 string
     */
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    /**
     * Convert private key to Base64 string
     */
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}

package com.bank.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {

    private static final String PEPPER = "GarvityBankSystemStaticPepper123!";

    // BCrypt hashing for credentials store
    public static String hashBCrypt(String plainPin) {
        if (plainPin == null) {
            return null;
        }
        return BCrypt.hashpw(plainPin, BCrypt.gensalt(10));
    }

    // BCrypt verification
    public static boolean verifyBCrypt(String plainPin, String hashedPin) {
        if (plainPin == null || hashedPin == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPin, hashedPin);
        } catch (Exception e) {
            return false;
        }
    }

    // Deterministic SHA-256 hashing for database mapping queries
    public static String sha256(String plainPin) {
        if (plainPin == null) {
            return null;
        }
        try {
            String input = plainPin + PEPPER;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed: " + e.getMessage(), e);
        }
    }
}

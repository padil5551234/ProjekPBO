package com.example.uji_coba;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    /**
     * Hashes a password using SHA-256.
     *
     * IMPORTANT: This method is insecure as it does not use a salt.
     * It is highly recommended to migrate to a stronger, salted hashing algorithm
     * like BCrypt or SCrypt for production environments.
     *
     * @param password The password to hash.
     * @return The hashed password as a hex string.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // In a real application, you'd want to handle this more gracefully
            // perhaps by logging the error and notifying the user.
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Checks if the provided plain password matches the stored hashed password.
     *
     * @param plainPassword The plain text password to check.
     * @param hashedPassword The stored hashed password.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String hashedPlainPassword = hashPassword(plainPassword);
        return hashedPlainPassword.equals(hashedPassword);
    }
}
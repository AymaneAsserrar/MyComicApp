package com.project.controller;

import com.project.util.Hashing;
import com.project.util.DatabaseUtil;
import com.project.util.EmailUtil;
import com.project.model.User;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class UserAuthController {

    // Hashmaps for storing verification tokens and their expiration times
    private static final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> codeExpirationTimes = new ConcurrentHashMap<>();

    // method that gets a user information by email from the database
    public static User getUserByEmail(String email) {
        String query = "SELECT id, email, password_hash, created_at FROM user WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                } else {
                    System.out.println("No account found with email: " + email);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve user, try again: " + e.getMessage());
            return null;
        }
    }

    // method that validates entered user email and password
    public static String validateCredentials(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) {
            return "No account found with email: " + email;
        }
        boolean passwordMatches = Hashing.checkPassword(password, user.getPasswordHash());
        if (!passwordMatches) {
            return "Incorrect password. Please try again.";
        }

        return "SUCCESS";
    }

    public static void createUser(String email, String passwordHash) {
        String query = "INSERT INTO user (email, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to create user: " + e.getMessage());
        }
    }

    // Generate a secure token
    private static String generateResetToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    // Method to initiate password reset using hashmaps
    public static boolean initiatePasswordReset(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            return false;
        }

        String resetToken = generateResetToken();
        long expirationTime = System.currentTimeMillis() + 60 * 60 * 1000; // 1 hour expiration
        verificationCodes.put(email, resetToken); // Store token in the hashmap
        codeExpirationTimes.put(email, expirationTime); // Store expiration time

        String subject = "Password Reset Request";
        String message = "Your password reset token is: " + resetToken;
        EmailUtil.sendEmail(email, subject, message);

        return true;
    }

    // Validate reset token using hashmaps
    public static boolean isResetTokenValid(String email, String token) {
        // Check if token exists and is not expired
        String storedToken = verificationCodes.get(email);
        Long expirationTime = codeExpirationTimes.get(email);

        if (storedToken != null && expirationTime != null) {
            if (storedToken.equals(token) && System.currentTimeMillis() <= expirationTime) {
                return true;
            }
        }
        return false;
    }

    // Update password in the database
    public static void updateUserPassword(String email, String newPassword) {
        String query = "UPDATE user SET password_hash = ? WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            String hashedPassword = Hashing.hashPassword(newPassword);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user password: " + e.getMessage());
        }
    }
}
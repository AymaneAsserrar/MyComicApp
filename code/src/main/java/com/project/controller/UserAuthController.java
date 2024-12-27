package com.project.controller;

import com.project.util.Hashing;
import com.project.util.DatabaseUtil;
import com.project.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthController {
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
}
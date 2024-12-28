package com.project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;

public class DatabaseUtil {
    private static final String DB_NAME = "comicApp.db";
    private static final String DB_URL;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
        }

        String dbPath;
        File resourcesDir = new File("src/main/resources");
        if (!resourcesDir.exists()) {
            resourcesDir = new File("code/src/main/resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            dbPath = resourcesDir.getAbsolutePath() + File.separator + DB_NAME;
        } else {
            dbPath = resourcesDir.getAbsolutePath() + File.separator + DB_NAME;
        }

        DB_URL = "jdbc:sqlite:" + dbPath;
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Connection to the database failed: " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String createComicsTable = "CREATE TABLE IF NOT EXISTS comic (" +
                "id_comic INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "image TEXT," +
                "genres TEXT)";

        String createBiblioTable = "CREATE TABLE IF NOT EXISTS biblio (" +
                "id_biblio INTEGER," +
                "id_comic INTEGER," +
                "status INTEGER," +
                "possede INTEGER," +
                "added INTEGER," +
                "FOREIGN KEY (id_biblio) REFERENCES user(id)," +
                "FOREIGN KEY (id_comic) REFERENCES comic(id_comic))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createComicsTable);
            stmt.execute(createBiblioTable);
            
            // Add admin account after creating tables
            createTestAccounts();
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    private static void createTestAccounts() {
        // Admin account parameters
        String adminEmail = "admin@admin.com";
        String adminPassword = "admin";
    
        // Test account parameters  
        String testEmail = "test@test.com";
        String testPassword = "test123";
    
        String checkQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        String insertQuery = "INSERT INTO user (email, password_hash) VALUES (?, ?)";
    
        try (Connection conn = getConnection()) {
            // Create admin account if it doesn't exist
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, adminEmail);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, adminEmail);
                        insertStmt.setString(2, Hashing.hashPassword(adminPassword));
                        insertStmt.executeUpdate();
                        System.out.println("Admin account created successfully");
                    }
                }
            }
    
            // Create test account if it doesn't exist
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, testEmail);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, testEmail);
                        insertStmt.setString(2, Hashing.hashPassword(testPassword));
                        insertStmt.executeUpdate();
                        System.out.println("Test account created successfully");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating test accounts: " + e.getMessage());
        }
    }
}
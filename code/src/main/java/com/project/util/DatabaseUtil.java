package com.project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URL;

public class DatabaseUtil {
    private static final String DB_NAME = "comicApp.db";
    private static final String DB_URL;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
        }

        // Determine the path to the resources directory
        URL resourceUrl = DatabaseUtil.class.getClassLoader().getResource("");
        if (resourceUrl != null) {
            String resourcePath = resourceUrl.getPath();
            DB_URL = "jdbc:sqlite:" + resourcePath + DB_NAME;
        } else {
            throw new RuntimeException("Failed to determine the resources directory path.");
        }
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
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "id_biblio INTEGER)";

        String createBiblioTable = "CREATE TABLE IF NOT EXISTS biblio (" +
                "id_biblio INTEGER," +
                "id_comic INTEGER," +
                "status INTEGER," +
                "possede INTEGER," +
                "added INTEGER)";

        String createComicsTable = "CREATE TABLE IF NOT EXISTS comic (" +
                "id_comic INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "image TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createBiblioTable);
            stmt.execute(createComicsTable);
        } catch (SQLException e) {
            System.err.println("Table creation failed: " + e.getMessage());
        }
    }
}
package com.project.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.project.util.DatabaseUtil;
import com.project.model.Comic;

public class UserLibraryController {

    /**
     * Adds a comic to the user's library
     */
    public boolean addComicToLibrary(int userId, Comic comic) {
        // First check if comic exists in comic table, if not add it
        ensureComicExists(comic);

        // Then add entry to biblio table
        String query = "INSERT OR REPLACE INTO biblio (id_biblio, id_comic, status, possede, added) " +
                "VALUES (?, ?, 1, 0, 1)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comic.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding comic to library: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if comic exists in comic table, adds if not
     */
    private void ensureComicExists(Comic comic) {
        String checkQuery = "SELECT COUNT(*) FROM comic WHERE id_comic = ?";
        String insertQuery = "INSERT INTO comic (id_comic, name, description, image) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check if comic exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, comic.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return; // Comic already exists
                }
            }

            // Insert comic if it doesn't exist
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, comic.getId());
                insertStmt.setString(2, comic.getName());
                insertStmt.setString(3, comic.getDescription());
                insertStmt.setString(4, comic.getCoverImageUrl());
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error ensuring comic exists in database: " + e.getMessage());
        }
    }

    /**
     * Checks if a comic is in user's library
     */
    public boolean isComicInLibrary(int userId, int comicId) {
        String query = "SELECT added FROM biblio WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("added") == 1;

        } catch (SQLException e) {
            System.err.println("Error checking comic in library: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a comic from user's library
     */
    public boolean removeComicFromLibrary(int userId, int comicId) {
        // First check if comic exists in user's library
        if (!isComicInLibrary(userId, comicId)) {
            return false; // Comic not in library, nothing to remove
        }

        // Delete entry from biblio table
        String query = "DELETE FROM biblio WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing comic from library: " + e.getMessage());
            return false;
        }
    }

    // Ownership status handling
    public boolean updateComicOwnership(int userId, int comicId, int possede) {
        if (!isComicInLibrary(userId, comicId)) {
            return false;
        }

        String query = "UPDATE biblio "
                + "SET possede = ? "
                + "WHERE id_biblio = ? AND id_comic = ?;";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, possede);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, comicId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating possede: " + e.getMessage());
            return false;
        }
    }
}
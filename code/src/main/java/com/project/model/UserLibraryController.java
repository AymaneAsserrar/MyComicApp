package com.project.model;

import java.sql.Connection;
import java.sql.DriverManager;
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
        ensureComicExists(comic);
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM biblio WHERE id_biblio = ? AND id_comic = ?");
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO biblio (id_biblio, id_comic, added) VALUES (?, ?, 1)")) {

            // Vérifier si existe déjà
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, comic.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false; // Déjà dans la bibliothèque
            }

            // Ajouter si n'existe pas
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, comic.getId());
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

    // Ownership handling

    public boolean updateComicOwnership(int userId, int comicId, int possede) {
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

    public boolean resetComicOwnership(int userId, int comicId) {
        if (!isComicInWishlist(userId, comicId)) {
            return false;
        }
        String query = "UPDATE biblio "
                + "SET possede = NULL "
                + "WHERE id_biblio = ? AND id_comic = ?;";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating possede: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a comic is in user's wishlist
     */
    public boolean isComicInWishlist(int userId, int comicId) {
        String query = "SELECT COUNT(*) FROM biblio WHERE id_biblio = ? AND id_comic = ? AND possede = 0";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
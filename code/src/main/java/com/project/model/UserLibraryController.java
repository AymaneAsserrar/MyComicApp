package com.project.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.project.util.DatabaseUtil;
import com.project.controller.SearchController;
import com.project.model.Comic;

public class UserLibraryController {

    /**
     * Adds a comic to the user's library
     */
    public boolean addComicToLibrary(int userId, Comic comic) {
        // Get full comic details to ensure genres are loaded
        SearchController searchController = new SearchController();
        Comic detailedComic = searchController.getComicDetails(comic.getId());
        if (detailedComic != null) {
            comic.setGenres(detailedComic.getGenres());
        }
        ensureComicExists(comic);
        String checkQuery = "SELECT COUNT(*) FROM biblio WHERE id_biblio = ? AND id_comic = ?";
        String insertQuery = "INSERT INTO biblio (id_biblio, id_comic, added) VALUES (?, ?, 1)";
        String updateQuery = "UPDATE biblio SET added = 1 WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM biblio WHERE id_biblio = ? AND id_comic = ?");
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO biblio (id_biblio, id_comic, added) VALUES (?, ?, 1)")) {

            // Vérifier si existe déjà
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, comic.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, userId);
                    updateStmt.setInt(2, comic.getId());
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, comic.getId());
                    return insertStmt.executeUpdate() > 0;
                }
            }

            // Ajouter si n'existe pas
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, comic.getId());
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding comic to library: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the ownership status of a comic in the user's library
     */
    public boolean updateComicOwnership(int userId, int comicId, Integer possede) {
        String checkQuery = "SELECT COUNT(*) FROM biblio WHERE id_biblio = ? AND id_comic = ?";
        String insertQuery = "INSERT INTO biblio (id_biblio, id_comic, possede) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE biblio SET possede = ? WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, comicId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    if (possede == null) {
                        updateStmt.setNull(1, java.sql.Types.INTEGER);
                    } else {
                        updateStmt.setInt(1, possede);
                    }
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, comicId);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, comicId);
                    if (possede == null) {
                        insertStmt.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        insertStmt.setInt(3, possede);
                    }
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating ownership: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ensures the comic exists in the database
     */
    private void ensureComicExists(Comic comic) {
        // Add debug logging
        System.out.println("Ensuring comic exists with genres: " + comic.getGenresAsString());

        String checkQuery = "SELECT COUNT(*) FROM comic WHERE id_comic = ?";
        String insertQuery = "INSERT INTO comic (id_comic, name, description, image, genres) VALUES (?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE comic SET genres = ? WHERE id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check if comic exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, comic.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Comic exists - update genres
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, comic.getGenresAsString());
                        updateStmt.setInt(2, comic.getId());
                        updateStmt.executeUpdate();
                    }
                    return;
                }
            }

            // Insert comic if it doesn't exist
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, comic.getId());
                insertStmt.setString(2, comic.getName());
                insertStmt.setString(3, comic.getDescription());
                insertStmt.setString(4, comic.getCoverImageUrl());
                insertStmt.setString(5, comic.getGenresAsString());
                insertStmt.executeUpdate();
                System.out.println("Inserted comic with genres: " + comic.getGenresAsString());
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

    public boolean removeComicFromLibrary(int userId, int comicId) {
        String query = "UPDATE biblio SET added = 0 WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error removing comic from library: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a comic is owned by the user
     */
    public boolean isComicOwned(int userId, int comicId) {
        String query = "SELECT possede FROM biblio WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("possede") == 1;

        } catch (SQLException e) {
            System.err.println("Error checking comic ownership: " + e.getMessage());
            return false;
        }
    }

    public boolean isComicInWishlist(int userId, int comicId) {
        String query = "SELECT possede FROM biblio WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("possede") == 0;

        } catch (SQLException e) {
            System.err.println("Error checking comic in wishlist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Resets the ownership status of a comic in the user's library
     */
    public boolean resetComicOwnership(int userId, int comicId) {
        String query = "UPDATE biblio SET possede = NULL WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error resetting comic ownership: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the comic status for the user
     */
    /**
     * Gets the comic status for the user
     */
    public String getComicStatus(int userId, int comicId) {
        String query = "SELECT added, possede FROM biblio WHERE id_biblio = ? AND id_comic = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, comicId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Integer possede = rs.getObject("possede") != null ? rs.getInt("possede") : null;
                if (possede != null && possede == 1) {
                    return "owned";  // Return icon path "owned.png"
                } else if (possede != null && possede == 0) {
                    return "ystar";  // In wishlist
                } else {
                    return "wstar";  // Not in wishlist or library
                }
            }
            return "wstar"; // Default: not in library

        } catch (SQLException e) {
            System.err.println("Error checking comic status: " + e.getMessage());
            return "wstar";
        }
    }
}
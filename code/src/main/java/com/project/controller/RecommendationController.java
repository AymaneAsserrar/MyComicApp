package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.project.api.API;
import com.project.model.Comic;
import com.project.model.Hero;
import com.project.model.UserLibraryController;
import com.project.util.DatabaseUtil;

import kotlin.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecommendationController {
    private API api;

    // Constructeur pour initialiser l'API
    public RecommendationController() {
        this.api = new API();
    }

    public Comic getComicDetails(int comicId) {
        String jsonResponse = api.getComicDetails(comicId);
        if (jsonResponse == null) {
            return null;
        }

        Gson gson = new Gson();
        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonObject resultsObject = responseObject.getAsJsonObject("results");

        return api.parseComicDetails(resultsObject);
    }

    // Méthode pour obtenir la liste des recommandations de comics populaires avec
    // une limite configurable
    public List<Comic> getPopularComics(int offset, int limit) {
        String jsonResponse = api.getPopularComics(offset, limit);
        if (jsonResponse == null) {
            return new ArrayList<>();
        }

        List<Comic> comicsList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");

        for (JsonElement element : resultsArray) {
            JsonObject volumeJson = element.getAsJsonObject();
            Comic comic = api.parseComicDetails(volumeJson);
            if (comic != null) {
                comicsList.add(comic);
            }
        }

        return comicsList;
    }

    public List<Comic> getComicsByGenres(String[] genres, int offset, int limit) {
        List<Comic> allComics = new ArrayList<>();
        int remainingLimit = limit;

        for (String genre : genres) {
            if (remainingLimit <= 0) {
                break;
            }

            try {
                List<Comic> comics = api.searchComicsByGenres(genre, offset, remainingLimit);
                allComics.addAll(comics);
                remainingLimit -= comics.size();
            } catch (IOException e) {
                System.err.println("Error getting comics by genre: " + genre + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        return allComics;
    }

    public Comic getComicDetailsFromIssue(int issueId) {
        int volumeId = api.getVolumeIdFromIssue(issueId);
        if (volumeId == -1) {
            return null;
        }
        return getComicDetails(volumeId);
    }

    public Pair<String, List<Comic>> getComicsFromSameVolume(int userId, int offset, int limit) {
        List<Comic> comics = new ArrayList<>();
        String selectedComicName = null;
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get random comic from user's library with status 0 or 1
            String query = "SELECT c.id_comic, c.name FROM comic c " +
                    "JOIN biblio b ON c.id_comic = b.id_comic " +
                    "WHERE b.id_biblio = ? AND (b.status = 0 OR b.status = 1) " +
                    "ORDER BY RANDOM() LIMIT 1";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int comicId = rs.getInt("id_comic");
                selectedComicName = rs.getString("name");
                System.out.println("Selected random comic ID: " + comicId); // Debug log

                // Get issues from volume using new endpoint
                String volumeResponse = api.getVolumeIssues(comicId, offset, limit);
                if (volumeResponse != null) {
                    System.out.println("Got volume issues response: " + volumeResponse); // Debug log
                    Gson gson = new Gson();
                    JsonObject volumeResponseObject = gson.fromJson(volumeResponse, JsonObject.class);
                    JsonArray issuesArray = volumeResponseObject.getAsJsonArray("results");

                    for (JsonElement element : issuesArray) {
                        JsonObject issueJson = element.getAsJsonObject();
                        Comic comic = api.parseComicDetails(issueJson);
                        if (comic != null) {
                            comics.add(comic);
                        }
                    }
                } else {
                    System.err.println("No issues found in volume response");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in getComicsFromSameVolume: " + e.getMessage());
            e.printStackTrace();
        }
        return new Pair<>(selectedComicName, comics);
    }

    public List<Comic> getRecommendedComics(int userId, int offset, int limit) {
        List<Comic> recommendations = new ArrayList<>();
        int remaining = limit;

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get user's preferred genres from library
            String genreQuery = "SELECT DISTINCT c.genres FROM comic c " +
                    "JOIN biblio b ON c.id_comic = b.id_comic " +
                    "WHERE b.id_biblio = ? AND b.added = 1 AND c.genres IS NOT NULL AND c.genres != ''";

            PreparedStatement stmt = conn.prepareStatement(genreQuery);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<String> userGenres = new ArrayList<>();
            while (rs.next()) {
                String genres = rs.getString("genres");
                if (genres != null && !genres.isEmpty()) {
                    userGenres.addAll(Arrays.asList(genres.split(",")));
                }
            }

            // Get recommendations for each genre
            for (String genre : userGenres) {
                if (remaining <= 0)
                    break;

                try {
                    // Calculate limit for this genre
                    List<Comic> genreComics = api.searchComicsByGenres(genre.trim(), offset, remaining);

                    // Filter out comics already in library
                    genreComics.removeIf(comic -> isComicInLibrary(userId, comic.getId()));

                    recommendations.addAll(genreComics);
                    remaining -= genreComics.size();

                } catch (IOException e) {
                    System.err.println("Error getting recommendations for genre: " + genre);
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while getting recommendations: " + e.getMessage());
            e.printStackTrace();
        }

        return recommendations;
    }

    private boolean isComicInLibrary(int userId, int comicId) {
        UserLibraryController controller = new UserLibraryController();
        return controller.isComicInLibrary(userId, comicId);
    }
}

package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.project.api.API;
import com.project.model.Comic;
import com.project.model.Hero;
import com.project.util.DatabaseUtil;

import kotlin.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get genres from user's library
            String genreQuery = "SELECT DISTINCT c.genres FROM comic c " +
                    "JOIN biblio ul ON c.id_comic = ul.id_comic " +
                    "WHERE ul.id_biblio = ? AND added = 1";

            PreparedStatement stmt = conn.prepareStatement(genreQuery);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<String> genres = new ArrayList<>();
            while (rs.next()) {
                String genre = rs.getString("genres");
                if (genre != null && !genre.isEmpty()) {
                    genres.add(genre);
                }
            }

            if (!genres.isEmpty()) {
                // Get recommendations based on genres
                String[] genreArray = genres.toArray(new String[0]);
                recommendations = getComicsByGenres(genreArray, offset, limit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendations;
    }
}

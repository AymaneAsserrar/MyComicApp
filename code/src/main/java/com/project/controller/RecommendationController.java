package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.project.api.API;
import com.project.model.Comic;
import com.project.model.Hero;
import com.project.util.DatabaseUtil;

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
    // Méthode pour obtenir la liste des recommandations de comics populaires avec une limite configurable
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
        StringBuilder genreQuery = new StringBuilder();
        for (String genre : genres) {
            if (genreQuery.length() > 0) {
                genreQuery.append(",");
            }
            genreQuery.append(genre.trim());
        }
        
        String jsonResponse = api.searchComicsByGenres(genreQuery.toString(), offset, limit);
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

    public List<Comic> getRecommendedComics(int userId, int offset, int limit) {
        List<Comic> recommendations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get genres from user's library
            String genreQuery = "SELECT DISTINCT c.genres FROM comic c " +
                              "JOIN biblio ul ON c.id_comic = ul.id_comic " +
                              "WHERE ul.id_biblio = ?";
            
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

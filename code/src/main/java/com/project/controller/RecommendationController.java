package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.project.api.API;
import com.project.model.Comic;
import com.project.model.Hero;

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



}

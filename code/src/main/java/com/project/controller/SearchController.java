package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.api.API;
import com.project.model.Comic;
import com.project.model.Hero;

import java.util.ArrayList;
import java.util.List;

public class SearchController {
    public API api;

    public SearchController() {
        this.api = new API();
    }

    public SearchResult searchComicsByTitle(String title, int page, int limit) {
        String jsonResponse = api.searchComicsByTitle(title);
        if (jsonResponse == null) {
            return new SearchResult(new ArrayList<>(), 0);
        }

        List<Comic> comicsList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");
        int totalResults = responseObject.get("number_of_total_results").getAsInt();

        for (JsonElement element : resultsArray) {
            JsonObject volumeJson = element.getAsJsonObject();

            Comic comic = new Comic();
            comic.setId(volumeJson.get("id").getAsInt());

            if (volumeJson.has("name") && !volumeJson.get("name").isJsonNull()) {
                comic.setName(volumeJson.get("name").getAsString());
            } else {
                comic.setName("Unknown Title");
            }

            if (volumeJson.has("image") && !volumeJson.get("image").isJsonNull()) {
                JsonObject imageJson = volumeJson.getAsJsonObject("image");
                if (imageJson.has("medium_url") && !imageJson.get("medium_url").isJsonNull()) {
                    comic.setCoverImageUrl(imageJson.get("medium_url").getAsString());
                } else {
                    comic.setCoverImageUrl("https://via.placeholder.com/150");
                }
            } else {
                comic.setCoverImageUrl("https://via.placeholder.com/150");
            }

            if (volumeJson.has("description") && !volumeJson.get("description").isJsonNull()) {
                comic.setDescription(volumeJson.get("description").getAsString());
            } else {
                comic.setDescription("No description available.");
            }

            comicsList.add(comic);
        }

        return new SearchResult(comicsList, totalResults);
    }

    public SearchResult searchCharactersByName(String name) {
        String jsonResponse = api.fetchCharacterData(name);
        if (jsonResponse == null) {
            return new SearchResult(new ArrayList<>(), 0);
        }

        List<Hero> heroesList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");
        int totalResults = responseObject.get("number_of_total_results").getAsInt();

        int limit = 12; // Limit the results to 12
        int count = 0;

        for (JsonElement element : resultsArray) {
            if (count >= limit) break;
            JsonObject volumeJson = element.getAsJsonObject();

            Hero hero = new Hero();
            hero.setId(volumeJson.get("id").getAsInt());

            if (volumeJson.has("name") && !volumeJson.get("name").isJsonNull()) {
                hero.setName(volumeJson.get("name").getAsString());
            } else {
                hero.setName("Unknown Character");
            }

            if (volumeJson.has("image") && !volumeJson.get("image").isJsonNull()) {
                JsonObject imageJson = volumeJson.getAsJsonObject("image");
                if (imageJson.has("medium_url") && !imageJson.get("medium_url").isJsonNull()) {
                    hero.setImageUrl(imageJson.get("medium_url").getAsString());
                } else {
                    hero.setImageUrl("https://via.placeholder.com/150");
                }
            } else {
                hero.setImageUrl("https://via.placeholder.com/150");
            }

            if (volumeJson.has("description") && !volumeJson.get("description").isJsonNull()) {
                hero.setDescription(volumeJson.get("description").getAsString());
            } else {
                hero.setDescription("No description available.");
            }

            heroesList.add(hero);
            count++;
        }

        return new SearchResult(heroesList, totalResults);
    }

    public static class SearchResult {
        private List<?> results;
        private int totalResults;

        public SearchResult(List<?> results, int totalResults) {
            this.results = results;
            this.totalResults = totalResults;
        }

        public List<?> getResults() {
            return results;
        }

        public int getTotalResults() {
            return totalResults;
        }
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
    
    public Hero getCharacterDetails(int characterId) {
        String jsonResponse = api.fetchCharacterDetails(characterId); 
        if (jsonResponse == null) {
            return null; 
        }
        Gson gson = new Gson();
        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        if (responseObject.has("results") && responseObject.get("results").isJsonObject()) {
            JsonObject resultsObject = responseObject.getAsJsonObject("results");
            Hero hero = new Hero();
            hero.setId(resultsObject.get("id").getAsInt());
            hero.setName(resultsObject.get("name").getAsString());
            hero.setDescription(resultsObject.has("description") ? resultsObject.get("description").getAsString() : null);
            hero.setImageUrl(resultsObject.has("image") ? resultsObject.getAsJsonObject("image").get("original_url").getAsString() : null);
    
            return hero; 
        }
    
        return null; 
    }
    
}
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
    private API api;

    public SearchController() {
        this.api = new API();
    }

    public List<Comic> searchComicsByTitle(String title) {
        String jsonResponse = api.searchComicsByTitle(title);
        if (jsonResponse == null) {
            return new ArrayList<>();
        }

        List<Comic> comicsList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");

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

        return comicsList;
    }

    // Placeholder for character search (not yet implemented)
    public List<Hero> searchCharactersByName(String name) {

        String jsonResponse = api.fetchCharacterData(name);
        if (jsonResponse == null) {
            return new ArrayList<>();
        }

        List<Hero> herosList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");

        for (JsonElement element : resultsArray) {
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

            herosList.add(hero);
        }

        return herosList;
    }
}
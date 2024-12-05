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

    // Méthode pour obtenir la liste des recommandations de comics populaires avec une limite configurable
    public List<Comic> getPopularComics(int limit) {
        String jsonResponse = api.getPopularComics(limit);
        if (jsonResponse == null) {
            return new ArrayList<>(); // Retourne une liste vide si la réponse est nulle
        }

        List<Comic> comicsList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");

        for (JsonElement element : resultsArray) {
            JsonObject volumeJson = element.getAsJsonObject();
            Comic comic = parseComicDetails(volumeJson);
            comicsList.add(comic);
        }

        return comicsList;
    }

    private Comic parseComicDetails(JsonObject volumeJson) {
        Comic comic = new Comic();
        comic.setId(volumeJson.get("id").getAsInt());
        comic.setName(getJsonString(volumeJson, "name", "Unknown Title"));
        comic.setDescription(getJsonString(volumeJson, "description", "No description available."));
        comic.setDeck(getJsonString(volumeJson, "deck", ""));

        // Parse image
        if (volumeJson.has("image") && !volumeJson.get("image").isJsonNull()) {
            JsonObject imageJson = volumeJson.getAsJsonObject("image");
            comic.setCoverImageUrl(getJsonString(imageJson, "medium_url", "https://via.placeholder.com/150"));
        }

        // Parse authors
        if (volumeJson.has("people") && !volumeJson.get("people").isJsonNull()) {
            JsonArray peopleArray = volumeJson.getAsJsonArray("people");
            for (JsonElement personElement : peopleArray) {
                JsonObject person = personElement.getAsJsonObject();
                String role = getJsonString(person, "role", "");
                if (role.equalsIgnoreCase("writer") || role.equalsIgnoreCase("artist")) {
                    comic.addAuthor(getJsonString(person, "name", ""));
                }
            }
        }

        // Parse characters
        if (volumeJson.has("characters") && !volumeJson.get("characters").isJsonNull()) {
            JsonArray charactersArray = volumeJson.getAsJsonArray("characters");
            List<Hero> charactersList = new ArrayList<>();
            for (JsonElement charElement : charactersArray) {
                JsonObject charJson = charElement.getAsJsonObject();
                Hero character = new Hero();
                character.setId(charJson.get("id").getAsInt());
                character.setName(getJsonString(charJson, "name", ""));
                character.setRealName(getJsonString(charJson, "real_name", ""));
                charactersList.add(character);
            }
            comic.setHeroes(charactersList);
        }

        return comic;
    }

    private String getJsonString(JsonObject json, String key, String defaultValue) {
        return json.has(key) && !json.get(key).isJsonNull() ? 
               json.get(key).getAsString() : defaultValue;
    }
}

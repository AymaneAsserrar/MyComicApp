package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.project.api.API;
import com.project.model.Comic;

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
            JsonObject comicJson = element.getAsJsonObject();
            Comic comic = new Comic();
            comic.setId(comicJson.get("id").getAsInt());

            // Vérifier la valeur avant de l'utiliser pour éviter une exception
            if (comicJson.has("name") && !comicJson.get("name").isJsonNull()) {
                comic.setName(comicJson.get("name").getAsString());
            } else {
                comic.setName("Unknown Title");
            }

            JsonObject imageJson = comicJson.getAsJsonObject("image");
            if (imageJson != null && imageJson.has("thumb_url") && !imageJson.get("thumb_url").isJsonNull()) {
                comic.setCoverImageUrl(imageJson.get("thumb_url").getAsString());
            } else {
                comic.setCoverImageUrl("https://via.placeholder.com/150"); // Image par défaut si indisponible
            }

            if (comicJson.has("description") && !comicJson.get("description").isJsonNull()) {
                comic.setDescription(comicJson.get("description").getAsString());
            } else {
                comic.setDescription("No description available.");
            }

            comicsList.add(comic);
        }

        return comicsList;
    }
}

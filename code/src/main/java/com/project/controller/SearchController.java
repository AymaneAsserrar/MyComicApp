package com.project.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.api.API;
import com.project.model.Comic;

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
            return new ArrayList<>(); // Retourne une liste vide si la réponse est nulle
        }

        List<Comic> comicsList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");

        for (JsonElement element : resultsArray) {
            JsonObject volumeJson = element.getAsJsonObject();

            // Initialiser un nouvel objet Comic
            Comic comic = new Comic();
            comic.setId(volumeJson.get("id").getAsInt());

            // Vérifier si le champ "name" est présent
            if (volumeJson.has("name") && !volumeJson.get("name").isJsonNull()) {
                comic.setName(volumeJson.get("name").getAsString());
            } else {
                comic.setName("Unknown Title");
            }

            // Vérifier s'il y a une image disponible
            if (volumeJson.has("image") && !volumeJson.get("image").isJsonNull()) {
                JsonObject imageJson = volumeJson.getAsJsonObject("image");
                if (imageJson.has("medium_url") && !imageJson.get("medium_url").isJsonNull()) {
                    comic.setCoverImageUrl(imageJson.get("medium_url").getAsString());
                } else {
                    comic.setCoverImageUrl("https://via.placeholder.com/150"); // URL d'image par défaut
                }
            } else {
                comic.setCoverImageUrl("https://via.placeholder.com/150"); // URL d'image par défaut
            }

            // Vérifier si une description est présente
            if (volumeJson.has("description") && !volumeJson.get("description").isJsonNull()) {
                comic.setDescription(volumeJson.get("description").getAsString());
            } else {
                comic.setDescription("No description available.");
            }

            // Ajouter le volume à la listeee
            comicsList.add(comic);
        }

        return comicsList;
    }

    public List<Comic> searchComicsByTitle(String title, int page, int limit) {
        String jsonResponse = api.searchComicsByTitle(title, page * limit, limit);
        if (jsonResponse == null) {
            return new ArrayList<>();
        }

        List<Comic> comicsList = new ArrayList<>();
        Gson gson = new Gson();

        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray resultsArray = responseObject.getAsJsonArray("results");

        for (JsonElement element : resultsArray) {
            JsonObject volumeJson = element.getAsJsonObject();

            // Initialiser un nouvel objet Comic
            Comic comic = new Comic();
            comic.setId(volumeJson.get("id").getAsInt());

            // Vérifier si le champ "name" est présent
            if (volumeJson.has("name") && !volumeJson.get("name").isJsonNull()) {
                comic.setName(volumeJson.get("name").getAsString());
            } else {
                comic.setName("Unknown Title");
            }

            // Vérifier s'il y a une image disponible
            if (volumeJson.has("image") && !volumeJson.get("image").isJsonNull()) {
                JsonObject imageJson = volumeJson.getAsJsonObject("image");
                if (imageJson.has("medium_url") && !imageJson.get("medium_url").isJsonNull()) {
                    comic.setCoverImageUrl(imageJson.get("medium_url").getAsString());
                } else {
                    comic.setCoverImageUrl("https://via.placeholder.com/150"); // URL d'image par défaut
                }
            } else {
                comic.setCoverImageUrl("https://via.placeholder.com/150"); // URL d'image par défaut
            }

            // Vérifier si une description est présente
            if (volumeJson.has("description") && !volumeJson.get("description").isJsonNull()) {
                comic.setDescription(volumeJson.get("description").getAsString());
            } else {
                comic.setDescription("No description available.");
            }

            // Ajouter le volume à la listeee
            comicsList.add(comic);
        }

        return comicsList;
    }
}
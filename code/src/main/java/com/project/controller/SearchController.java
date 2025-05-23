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

    /**
     * Searches for comics by their title using the provided API and returns a paginated result.
     *
     * @param title the title of the comic to search for
     * @param page the page number to retrieve (not currently used in the implementation)
     * @param limit the number of results per page (not currently used in the implementation)
     * @return a SearchResult object containing a list of comics and the total number of results
     */
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

    /**
     * Searches for characters by their name using an external API and returns the search results.
     *
     * @param name The name of the character to search for.
     * @return A SearchResult object containing a list of Hero objects and the total number of results.
     *
     * The method performs the following steps:
     * 1. Fetches character data from an external API using the provided name.
     * 2. Parses the JSON response to extract character information.
     * 3. Limits the results to a maximum of 12 characters.
     * 4. For each character, extracts and sets the ID, name, image URL, and description.
     * 5. If any field is missing or null, sets default values (e.g., "Unknown Character" for name, 
     *    "https://via.placeholder.com/150" for image URL, and "No description available." for description).
     * 6. Returns a SearchResult object containing the list of Hero objects and the total number of results.
     */
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
        System.out.println("API Response: " + jsonResponse); 
        if (jsonResponse == null) {
            return null; 
        }
    
        Gson gson = new Gson();
        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
    
        if (responseObject.has("results") && responseObject.get("results").isJsonObject()) {
            JsonObject resultsObject = responseObject.getAsJsonObject("results");
            Hero hero = new Hero();
    
            // Récupération de l'ID avec vérification
            if (resultsObject.has("id") && !resultsObject.get("id").isJsonNull()) {
                hero.setId(resultsObject.get("id").getAsInt());
            } else {
                hero.setId(-1); // Valeur par défaut pour ID non défini
            }
    
            // Récupération du nom avec vérification
            hero.setName(resultsObject.has("name") && !resultsObject.get("name").isJsonNull() ? 
                         resultsObject.get("name").getAsString() : "Unknown");
    
            // Récupération de la description
            hero.setDescription(resultsObject.has("description") && !resultsObject.get("description").isJsonNull() ? 
                                resultsObject.get("description").getAsString() : "No description available");
    
            // Récupération de l'URL de l'image
            if (resultsObject.has("image") && resultsObject.get("image").isJsonObject()) {
                JsonObject imageObject = resultsObject.getAsJsonObject("image");
                hero.setImageUrl(imageObject.has("original_url") && !imageObject.get("original_url").isJsonNull() ? 
                                 imageObject.get("original_url").getAsString() : null);
            } else {
                hero.setImageUrl(null);
            }
    
            // Récupération des titres
            if (resultsObject.has("issue_credits") && resultsObject.get("issue_credits").isJsonArray()) {
                JsonArray issueArray = resultsObject.getAsJsonArray("issue_credits");
                List<String> titles = new ArrayList<>();
                for (JsonElement issueElement : issueArray) {
                    if (issueElement.isJsonObject()) {
                        JsonObject issue = issueElement.getAsJsonObject();
                        if (issue.has("name") && !issue.get("name").isJsonNull()) {
                            titles.add(issue.get("name").getAsString());
                        }
                    }
                }
                hero.setTitles(titles.isEmpty() ? new String[]{"No associated titles found"} : titles.toArray(new String[0]));
            } else {
                hero.setTitles(new String[]{"No associated titles found"});
            }
    
            return hero; 
        }
    
        return null; 
    }
}
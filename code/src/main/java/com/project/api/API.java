package com.project.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.model.Comic;
import com.project.model.Hero;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class API {
    private static final String BASE_URL = "https://comicvine.gamespot.com/api/";
    private static final String API_KEY = "e22bd0a8fe36c642d47999f6e61f8e252a717ec7";
    private static OkHttpClient client;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

	public String getPopularComics(int limit) {
		String fieldList = "id,name,description,deck,image,characters,count_of_issues," +
		"date_added,date_last_updated,first_issue,last_issue," +
		"publisher,start_year,team_credits,rating";

		String endpoint = "volumes/?api_key=" + API_KEY 
				+ "&format=json&sort=date_added:desc&limit=" + limit 
				+ "&field_list=" + fieldList;
		String url = BASE_URL + endpoint;
	
		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();
	
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Request error: HTTP Code " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getComicDetails(int comicId) {
		String fieldList = "id,name,description,deck,image,characters,count_of_issues," +
		"date_added,date_last_updated,first_issue,last_issue," +
		"publisher,start_year,team_credits,rating";

		String endpoint = "volume/4050-" + comicId + "/?api_key=" + API_KEY
				+ "&format=json&field_list=" + fieldList;
		String url = BASE_URL + endpoint;
	
		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();
	
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Request error: HTTP Code " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
    public Comic parseComicDetails(JsonObject volumeJson) {
		Comic comic = new Comic();
		
		// Basic fields with null checks
		comic.setId(volumeJson.has("id") ? volumeJson.get("id").getAsInt() : 0);
		comic.setName(getJsonString(volumeJson, "name", "Unknown Title"));
		comic.setDeck(getJsonString(volumeJson, "deck", "No summary available."));
		comic.setDescription(getJsonString(volumeJson, "description", "No description available."));
	
		// Parse image
		if (volumeJson.has("image") && !volumeJson.get("image").isJsonNull()) {
			JsonObject imageJson = volumeJson.getAsJsonObject("image");
			// Try different image sizes in order of preference
			String imageUrl = getJsonString(imageJson, "medium_url", null);
			if (imageUrl == null) imageUrl = getJsonString(imageJson, "screen_url", null);
			if (imageUrl == null) imageUrl = getJsonString(imageJson, "super_url", null);
			if (imageUrl == null) imageUrl = getJsonString(imageJson, "original_url", "https://via.placeholder.com/150");
			comic.setCoverImageUrl(imageUrl);
		}
	
		// Parse characters array into heroes
		if (volumeJson.has("characters") && !volumeJson.get("characters").isJsonNull()) {
			JsonArray charactersArray = volumeJson.getAsJsonArray("characters");
			List<Hero> charactersList = new ArrayList<>();
			for (JsonElement charElement : charactersArray) {
				JsonObject charJson = charElement.getAsJsonObject();
				Hero hero = new Hero();
				hero.setId(charJson.has("id") ? charJson.get("id").getAsInt() : 0);
				hero.setName(getJsonString(charJson, "name", ""));
				// Get site_detail_url for more info
				String siteUrl = getJsonString(charJson, "site_detail_url", "");
				if (!siteUrl.isEmpty()) {
					hero.setImageUrl(siteUrl);
				}
				charactersList.add(hero);
			}
			comic.setHeroes(charactersList);
		}
	
		// Set other fields that might be available from the full volume endpoint
		if (volumeJson.has("count_of_issues")) {
			comic.setIssueCount(volumeJson.get("count_of_issues").getAsInt());
		}
	
		if (volumeJson.has("publisher") && !volumeJson.get("publisher").isJsonNull()) {
			JsonObject publisherJson = volumeJson.getAsJsonObject("publisher");
			comic.setPublisherName(getJsonString(publisherJson, "name", "Unknown Publisher"));
		}
	
		// Set dates
		comic.setDateAdded(getJsonString(volumeJson, "date_added", "Unknown"));
		comic.setDateLastUpdated(getJsonString(volumeJson, "date_last_updated", "Unknown"));
	
		// Set issue info
		if (volumeJson.has("first_issue") && !volumeJson.get("first_issue").isJsonNull()) {
			JsonObject firstIssue = volumeJson.getAsJsonObject("first_issue");
			comic.setFirstIssue(getJsonString(firstIssue, "name", "Unknown"));
		}
	
		if (volumeJson.has("last_issue") && !volumeJson.get("last_issue").isJsonNull()) {
			JsonObject lastIssue = volumeJson.getAsJsonObject("last_issue");
			comic.setLastIssue(getJsonString(lastIssue, "name", "Unknown"));
		}
	
		comic.setStartYear(getJsonString(volumeJson, "start_year", "Unknown"));
	
		// Set teams
		if (volumeJson.has("team_credits") && !volumeJson.get("team_credits").isJsonNull()) {
			JsonArray teamsArray = volumeJson.getAsJsonArray("team_credits");
			List<String> teamsList = new ArrayList<>();
			for (JsonElement teamElement : teamsArray) {
				JsonObject teamJson = teamElement.getAsJsonObject();
				String teamName = getJsonString(teamJson, "name", "");
				if (!teamName.isEmpty()) {
					teamsList.add(teamName);
				}
			}
			comic.setTeams(teamsList);
		}
	
		// Set rating
		comic.setRating(getJsonString(volumeJson, "rating", "N/A"));
	
		return comic;
	}

    private String getJsonString(JsonObject json, String key, String defaultValue) {
        if (json != null && json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return defaultValue;
    }
    public String searchComicsByTitle(String title) {
        String fieldList = "id,name,description,deck,image,person_credits,character_credits,rating";
        String endpoint = "search/?api_key=" + API_KEY 
                + "&format=json&resources=volume&query=" + title
                + "&field_list=" + fieldList;
        String url = BASE_URL + endpoint;

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "ComicApp/1.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request error: HTTP Code " + response.code());
            }
            return response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

	// Méthode pour chercher des personnages
	public String fetchCharacterData(String name) {
		String endpoint = "characters/?api_key=" + API_KEY + "&format=json&limit=10&offset=0&filter=name:" + name;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Erreur dans la requête: Code HTTP " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}

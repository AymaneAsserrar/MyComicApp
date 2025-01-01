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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public String getPopularComics(int offset, int limit) {
		String fieldList = "id,name,description,deck,image,characters,count_of_issues," +
				"date_added,date_last_updated,first_issue,last_issue," +
				"publisher,start_year,character_credits,rating";

		String endpoint = "volumes/?api_key=" + API_KEY
				+ "&format=json&sort=date_added:desc&offset=" + offset + "&limit=" + limit
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
				"publisher,start_year,character_credits,rating,concepts"; // Add concepts to fieldList

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
			if (imageUrl == null)
				imageUrl = getJsonString(imageJson, "screen_url", null);
			if (imageUrl == null)
				imageUrl = getJsonString(imageJson, "super_url", null);
			if (imageUrl == null)
				imageUrl = getJsonString(imageJson, "original_url", "https://via.placeholder.com/150");
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

		if (volumeJson.has("concepts") && !volumeJson.get("concepts").isJsonNull()) {
			JsonArray conceptsArray = volumeJson.getAsJsonArray("concepts");
			List<String> genresList = new ArrayList<>();
			for (JsonElement conceptElement : conceptsArray) {
				JsonObject conceptJson = conceptElement.getAsJsonObject();
				String genreName = getJsonString(conceptJson, "name", "");
				if (!genreName.isEmpty()) {
					genresList.add(genreName);
				}
			}
			comic.setGenres(genresList);
		}

		return comic;
	}

	private String getJsonString(JsonObject json, String key, String defaultValue) {
		if (json != null && json.has(key) && !json.get(key).isJsonNull()) {
			return json.get(key).getAsString();
		}
		return defaultValue;
	}

	public String searchComicsByTitle(String title) {
		String fieldList = "id,name,description,deck,image,person_credits," +
				"character_credits,rating,concepts"; // Add concepts to fieldList
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

	public String fetchCharacterDetails(int characterId) {
		String endpoint = "character/4005-" + characterId + "/?api_key=" + API_KEY + "&format=json";
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

	public Hero parseHeroDetails(JsonObject resultsObject) {
		Hero hero = new Hero();
		hero.setId(resultsObject.get("id").getAsInt());
		hero.setName(resultsObject.get("name").getAsString());
		hero.setRealName(resultsObject.has("real_name") ? resultsObject.get("real_name").getAsString() : "Unknown");
		hero.setImageUrl(resultsObject.getAsJsonObject("image").get("url").getAsString());
		hero.setDescription(resultsObject.has("description") ? resultsObject.get("description").getAsString()
				: "No description available");
		return hero;
	}

	public List<Comic> searchComicsByGenres(String genre, int offset, int limit) throws IOException {
		// Step 1: Fetch Concepts (Genres)
		List<Integer> conceptIds = fetchConceptIds(genre, limit);
		if (conceptIds.isEmpty()) {
			System.out.println("No concepts found for genre: " + genre);
			return new ArrayList<>();
		}

		System.out.println("Concept IDs: " + conceptIds);

		// Step 2: Fetch Issues by Concept ID
		Set<Integer> issueIds = new HashSet<>();
		for (Integer conceptId : conceptIds) {
			if (issueIds.size() >= limit) {
				break;
			}

			String fieldList = "id,name,description,issue_credits";
			String endpoint = "concept/4015-" + conceptId + "/?api_key=" + API_KEY
					+ "&format=json&field_list=" + fieldList + "&limit=" + limit;

			String url = BASE_URL + endpoint;

			System.out.println("Fetching issues with URL: " + url);

			// Get issues matching concept ID
			Request conceptRequest = new Request.Builder()
					.url(url)
					.header("User-Agent", "ComicApp/1.0")
					.header("Accept", "application/json")
					.build();

			Response conceptResponse = client.newCall(conceptRequest).execute();
			if (!conceptResponse.isSuccessful()) {
				throw new IOException("Error getting concept issues: HTTP " + conceptResponse.code());
			}

			String conceptJson = conceptResponse.body() != null ? conceptResponse.body().string() : null;
			if (conceptJson == null) {
				System.out.println("No issues found for concept ID: " + conceptId);
				continue;
			}

			System.out.println("Concept JSON: " + conceptJson);

			// Parse concept results
			JsonObject conceptObj = new Gson().fromJson(conceptJson, JsonObject.class);
			JsonObject conceptResults = conceptObj.getAsJsonObject("results");
			JsonArray issueCredits = conceptResults.getAsJsonArray("issue_credits");
			if (issueCredits != null) {
				for (JsonElement issue : issueCredits) {
					if (issueIds.size() >= limit) {
						break;
					}
					issueIds.add(issue.getAsJsonObject().get("id").getAsInt());
				}
			}
		}

		System.out.println("Issue IDs: " + issueIds);

		// Step 3: Fetch Comic Details by Issue ID
		List<Comic> comics = new ArrayList<>();
		List<Integer> limitedIds = new ArrayList<>(issueIds)
				.subList(Math.min(offset, issueIds.size()),
						Math.min(offset + limit, issueIds.size()));

		for (Integer issueId : limitedIds) {
			String comicDetailsJson = getIssueDetails(issueId);
			if (comicDetailsJson != null) {
				JsonObject comicObj = new Gson().fromJson(comicDetailsJson, JsonObject.class);
				JsonObject resultsObj = comicObj.getAsJsonObject("results");
				Comic comic = parseComicDetails(resultsObj);
				if (comic != null) {
					comics.add(comic);
				}
			}
		}

		return comics;
	}

	public String getIssueDetails(int issueId) {
		String fieldList = "id,name,description,deck,image,volume,concepts";
		String endpoint = "issue/4000-" + issueId + "/?api_key=" + API_KEY
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

	public int getVolumeIdFromIssue(int issueId) {
		String fieldList = "volume";
		String endpoint = "issue/4000-" + issueId + "/?api_key=" + API_KEY
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
			String jsonResponse = response.body() != null ? response.body().string() : null;
			if (jsonResponse != null) {
				JsonObject responseObj = new Gson().fromJson(jsonResponse, JsonObject.class);
				JsonObject resultsObj = responseObj.getAsJsonObject("results");
				JsonObject volumeObj = resultsObj.getAsJsonObject("volume");
				return volumeObj.get("id").getAsInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private List<Integer> fetchConceptIds(String genre, int limit) throws IOException {
		List<Integer> conceptIds = new ArrayList<>();
		String[] genres = genre.split(",");

		for (String g : genres) {
			String url = BASE_URL + "concepts/?api_key=" + API_KEY +
					"&format=json&field_list=id,name" +
					"&filter=name:" + g.trim() + "&limit=" + limit;

			// Log the URL for debugging
			System.out.println("Fetching concepts with URL: " + url);

			Request request = new Request.Builder()
					.url(url)
					.header("User-Agent", "ComicApp/1.0")
					.header("Accept", "application/json")
					.build();

			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new IOException("Error getting concepts: HTTP " + response.code());
			}

			String json = response.body().string();
			System.out.println("Response: " + json);

			JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
			JsonArray results = jsonObject.getAsJsonArray("results");

			for (JsonElement element : results) {
				JsonObject concept = element.getAsJsonObject();
				conceptIds.add(concept.get("id").getAsInt());
			}
		}

		return conceptIds;
	}

	public String getVolumeIssues(int comicId, int offset, int limit) {
		try {
			// Construct the correct URL with BASE_URL
			String endpoint = String.format("issues/?api_key=" + API_KEY +
					"&filter=volume:" + comicId
					+ "&format=json&offset=%d&limit=%d&field_list=id,name,description,deck,image", offset, limit);

			String url = BASE_URL + endpoint;

			System.out.println("Requesting URL: " + url); // Debug log

			Request request = new Request.Builder()
					.url(url)
					.header("User-Agent", "ComicApp/1.0")
					.header("Accept", "application/json")
					.build();

			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					System.err.println("Error response code: " + response.code()); // Debug log
					return null;
				}
				String responseBody = response.body() != null ? response.body().string() : null;
				System.out.println("Response received: " + (responseBody != null ? "yes" : "no")); // Debug log
				return responseBody;
			}
		} catch (IOException e) {
			System.err.println("Exception in getVolumeIssues: " + e.getMessage()); // Debug log
			e.printStackTrace();
			return null;
		}
	}
}

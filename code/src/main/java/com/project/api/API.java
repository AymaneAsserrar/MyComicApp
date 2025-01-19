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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The API class provides methods to interact with the ComicVine API.
 * It includes methods to fetch popular comics, comic descriptions, comic
 * details,
 * search comics by title, fetch character data, and search comics by genres.
 */
public class API {
	private static final String BASE_URL = "https://comicvine.gamespot.com/api/";
	private final APIKeyManager keyManager;
	private static OkHttpClient client;

	public API() {
		this.keyManager = new APIKeyManager();
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		client = new OkHttpClient.Builder()
				.addInterceptor(logging)
				.build();
	}

	/**
	 * Fetches a list of popular comics from the API.
	 * 
	 * This method retrieves popular comics based on the specified offset and limit.
	 * It first checks if the response is available in the cache. If not, it makes
	 * an
	 * API request to fetch the data. The response is then cached for future use.
	 * 
	 * @param offset the offset for pagination
	 * @param limit  the maximum number of comics to retrieve
	 * @return a JSON string containing the list of popular comics, or null if an
	 *         error occurs
	 */
	public String getPopularComics(int offset, int limit) {
		String cacheKey = "popularComics_" + offset + "_" + limit;
		String cachedResponse = APICache.get(cacheKey);
		if (cachedResponse != null) {
			return cachedResponse;
		}

		String fieldList = "id,name,description,deck,image,characters,count_of_issues," +
				"date_added,date_last_updated,first_issue,last_issue," +
				"publisher,start_year,character_credits,rating";

		String endpoint = "volumes/?api_key=" + keyManager.getCurrentKey("volumes")
				+ "&format=json&sort=date_added:desc&offset=" + offset + "&limit=" + limit
				+ "&field_list=" + fieldList;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("volumes");
				}
				throw new IOException("Request error: HTTP Code " + response.code());
			}
			String responseBody = response.body().string();
			APICache.put(cacheKey, responseBody);
			return responseBody;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getComicDescriptionAsHtml(int comicId) throws IOException {
		String url = BASE_URL + "volume/4050-" + comicId + "?api_key=" + keyManager.getCurrentKey("volume")
				+ "&format=xml&field_list=description";

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("volume");
				}
				System.out.println("API call failed: " + response.code());
				throw new IOException("API call failed: " + response.code());
			}

			String xmlResponse = response.body().string();
			System.out.println("API Response: " + xmlResponse); // Debug logging

			// Parse the XML response to extract the description
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
			NodeList descriptionNodes = doc.getElementsByTagName("description");
			String description = "";
			if (descriptionNodes.getLength() > 0) {
				description = descriptionNodes.item(0).getTextContent();
			}

			if (description != null && !description.trim().isEmpty()) {
				return "<html><body style='font-family: Arial; font-size: 14px; padding: 10px;'>"
						+ description
						+ "</body></html>";
			}

			return "<html><body>No detailed description available.</body></html>";
		} catch (Exception e) {
			System.out.println("Error fetching description: " + e.getMessage());
			e.printStackTrace();
			throw new IOException("Failed to fetch comic description", e);
		}
	}

	/**
	 * Retrieves the details of a comic based on the provided comic ID.
	 *
	 * @param comicId The ID of the comic to retrieve details for.
	 * @return A JSON string containing the details of the comic, or null if an
	 *         error occurs.
	 */
	public String getComicDetails(int comicId) {
		String fieldList = "id,name,description,deck,image,characters,count_of_issues," +
				"date_added,date_last_updated,first_issue,last_issue," +
				"publisher,start_year,character_credits,rating,concepts"; // Add concepts to fieldList

		String endpoint = "volume/4050-" + comicId + "/?api_key=" + keyManager.getCurrentKey("volume")
				+ "&format=json&field_list=" + fieldList;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("volume");
				}
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

	/**
	 * Searches for comics by their title.
	 *
	 * @param title The title of the comic to search for.
	 * @return A JSON string containing the search results, or null if an error
	 *         occurred.
	 */
	public String searchComicsByTitle(String title) {
		String fieldList = "id,name,description,deck,image,person_credits," +
				"character_credits,rating,concepts"; // Add concepts to fieldList
		String endpoint = "search/?api_key=" + keyManager.getCurrentKey("search")
				+ "&format=json&resources=volume&query=" + title
				+ "&field_list=" + fieldList;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("search");
				}
				throw new IOException("Request error: HTTP Code " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Méthode pour chercher des personnages
	/**
	 * Fetches character data from the API based on the provided character name.
	 *
	 * @param name the name of the character to search for
	 * @return a JSON string containing the character data, or null if an error occurs
	 */
	public String fetchCharacterData(String name) {
		String endpoint = "characters/?api_key=" + keyManager.getCurrentKey("characters")
				+ "&format=json&limit=10&offset=0&filter=name:" + name;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("characters");
				}
				throw new IOException("Erreur dans la requête: Code HTTP " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String fetchCharacterDetails(int characterId) {
		String endpoint = "character/4005-" + characterId + "/?api_key=" + keyManager.getCurrentKey("character")
				+ "&format=json";
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("character");
				}
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

	/**
	 * Searches for comics by genre.
	 *
	 * @param genre the genre to search for
	 * @param offset the offset for pagination
	 * @param limit the maximum number of results to return
	 * @return a list of comics that match the specified genre
	 * @throws IOException if an I/O error occurs during the search
	 *
	 * This method performs the following steps:
	 * 1. Fetches concept IDs for the specified genre.
	 * 2. Fetches issue IDs associated with the concept IDs.
	 * 3. Fetches comic details for the issue IDs and returns a list of comics.
	 *
	 * If no concepts are found for the specified genre, an empty list is returned.
	 * The method uses a key manager to obtain API keys and makes HTTP requests to fetch data.
	 * It parses the JSON responses to extract relevant information.
	 */
	public List<Comic> searchComicsByGenres(String genre, int offset, int limit) throws IOException {
		// Step 1: Fetch Concepts (Genres)
		List<Integer> conceptIds = fetchConceptIds(genre, 1);
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
			String endpoint = "concept/4015-" + conceptId + "/?api_key=" + keyManager.getCurrentKey("concept")
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

		List<Integer> VolumeIDs = new ArrayList<>();
		for (Integer issueId : limitedIds) {
			VolumeIDs.add(getVolumeIdFromIssue(issueId));
		}
		VolumeIDs = VolumeIDs.stream().distinct().toList();
		for (Integer volumeId : VolumeIDs) {
			String comicDetailsJson = getComicDetails(volumeId);
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
		String endpoint = "issue/4000-" + issueId + "/?api_key=" + keyManager.getCurrentKey("issue")
				+ "&format=json&field_list=" + fieldList;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("issue");
				}
				throw new IOException("Request error: HTTP Code " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getVolumeIdFromIssue(int issueId) {
		String cacheKey = "volumeIdFromIssue_" + issueId;
		String cachedValue = APICache.get(cacheKey);
		if (cachedValue != null) {
			return Integer.parseInt(cachedValue);
		}

		String fieldList = "volume";
		String endpoint = "issue/4000-" + issueId + "/?api_key=" + keyManager.getCurrentKey("issue")
				+ "&format=json&field_list=" + fieldList;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder()
				.url(url)
				.header("User-Agent", "ComicApp/1.0")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				if (response.code() == 420) {
					keyManager.forceKeyRotation("issue");
				}
				throw new IOException("Request error: HTTP Code " + response.code());
			}
			String jsonResponse = response.body() != null ? response.body().string() : null;
			if (jsonResponse != null) {
				JsonObject responseObj = new Gson().fromJson(jsonResponse, JsonObject.class);
				JsonObject resultsObj = responseObj.getAsJsonObject("results");
				if (resultsObj.has("volume")) {
					JsonObject volumeObj = resultsObj.getAsJsonObject("volume");
					int volumeId = volumeObj.get("id").getAsInt();
					APICache.put(cacheKey, String.valueOf(volumeId)); // cache result
					return volumeId;
				}
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
			String url = BASE_URL + "concepts/?api_key=" + keyManager.getCurrentKey("concepts") +
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
				if (response.code() == 420) {
					keyManager.forceKeyRotation("concepts");
				}
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
			String endpoint = String.format("issues/?api_key=" + keyManager.getCurrentKey("issues") +
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
					if (response.code() == 420) {
						keyManager.forceKeyRotation("issues");
					}
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

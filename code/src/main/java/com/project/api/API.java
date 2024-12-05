package com.project.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import com.google.gson.Gson;

import java.io.IOException;

public class API {
	private static final String BASE_URL = "https://comicvine.gamespot.com/api/";
	private static final String API_KEY = "e22bd0a8fe36c642d47999f6e61f8e252a717ec7";
	private static OkHttpClient client;

	// Initialisation du client HTTP avec un logging interceptor pour déboguer
	static {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		client = new OkHttpClient.Builder()
				.addInterceptor(logging)
				.build();
	}

	// Méthode pour obtenir des comics populaires avec une limite configurable
	public String getPopularComics(int limit) {
		String endpoint = "volumes/?api_key=" + API_KEY + "&format=json&sort=date_added:desc&limit=" + limit;
		String url = BASE_URL + endpoint;

		Request request = new Request.Builder().url(url).header("User-Agent", "ComicApp/1.0") // Ajout du User-Agent
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Erreur dans la requête: Code HTTP " + response.code());
			}
			return response.body() != null ? response.body().string() : null;
		} catch (IOException e) {
			e.printStackTrace();
			return null; // En cas d'erreur, retourne null
		}
	}

	// Méthode pour chercher des comics par titre
	public String searchComicsByTitle(String title) {
		String endpoint = "search/?api_key=" + API_KEY + "&format=json&resources=volume&query=" + title;
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

	// Méthode pour chercher des personnages avec offset et limite
	public String fetchCharacterData(String name, int offset, int limit) {
		String endpoint = String.format("characters/?api_key=%s&format=json&filter=name:%s&offset=%d&limit=%d",
				API_KEY, name, offset, limit);
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
	
	// Method for getting detailed comic info
    public String getComicDetails(int comicId) {
        String endpoint = "volume/4050-" + comicId + "/?api_key=" + API_KEY + "&format=json&field_list=name,description,image,characters,people,deck";
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
}

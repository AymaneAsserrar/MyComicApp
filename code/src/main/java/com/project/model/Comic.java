package com.project.model;

import java.util.ArrayList;
import java.util.List;

public class Comic {
    private int id;
    private String name;
    private String description;
    private String coverImageUrl;
    private List<String> authors;
    private String rating;
    private List<Hero> heroes;
    private String deck;

    public Comic() {
        this.authors = new ArrayList<>();
        this.rating = "N/A";
        this.heroes = new ArrayList<>();
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = cleanDescription(description);
    }

    public String getAuthorsAsString() {
        if (authors.isEmpty()) {
            return "Unknown";
        }
        return String.join(", ", authors);
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating != null ? rating : "N/A";
    }

    public List<Hero> getHero() {
        return heroes;
    }

    public void setHeroes(List<Hero> charactersList) {
        this.heroes = charactersList != null ? charactersList : new ArrayList<>();
    }

    public String getCharactersAsString() {
        if (heroes == null || heroes.isEmpty()) {
            return "No characters available";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Hero hero : heroes) {
            sb.append("• ").append(hero.getName());
            if (hero.getRealName() != null && !hero.getRealName().isEmpty()) {
                sb.append(" (").append(hero.getRealName()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String cleanDescription(String rawDescription) {
        if (rawDescription == null || rawDescription.isEmpty()) {
            return "No description available.";
        }

        // Remove HTML tags and clean entities
        String cleaned = rawDescription.replaceAll("<[^>]*>", "")
                                     .replace("&amp;", "&")
                                     .replace("&lt;", "<")
                                     .replace("&gt;", ">")
                                     .replace("&quot;", "\"")
                                     .replace("&apos;", "'")
                                     .replaceAll("\\s+", " ")
                                     .trim();

        return cleaned;
    }

    // Methods for authors
    public void addAuthor(String author) {
        if (author != null && !author.isEmpty()) {
            this.authors.add(author);
        }
    }

    // Methods for deck (short description)
    public String getDeck() {
        return deck == null ? "" : deck;
    }

    public void setDeck(String deck) {
        this.deck = cleanDescription(deck);
    }

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", description='" + description + '\'' +
                ", authors='" + getAuthorsAsString() + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}

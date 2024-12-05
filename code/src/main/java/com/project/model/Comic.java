package com.project.model;

import java.util.ArrayList;
import java.util.List;

public class Comic {
    private int id;
    private String name;
    private String description;
    private String coverImageUrl;
    private String authors;
    private String rating;
    private List<Character> characters;

    public Comic() {
        this.authors = "Unknown";
        this.rating = "N/A";
        this.characters = new ArrayList<>();
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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors != null ? authors : "Unknown";
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating != null ? rating : "N/A";
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters != null ? characters : new ArrayList<>();
    }

    public String getCharactersAsString() {
        if (characters == null || characters.isEmpty()) {
            return "No characters available";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Character character : characters) {
            sb.append("• ").append(character.getName());
            if (character.getRealName() != null && !character.getRealName().isEmpty()) {
                sb.append(" (").append(character.getRealName()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String cleanDescription(String rawDescription) {
        if (rawDescription == null || rawDescription.isEmpty()) {
            return "No description available.";
        }

        // Remove HTML tags
        String cleaned = rawDescription.replaceAll("<[^>]*>", "");
        
        // Convert HTML entities
        cleaned = cleaned.replace("&amp;", "&")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&quot;", "\"")
                        .replace("&apos;", "'");
        
        // Remove extra whitespace
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        
        // Split into paragraphs and rejoin with proper spacing
        String[] paragraphs = cleaned.split("\\n");
        cleaned = String.join("\n\n", paragraphs);

        return cleaned;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", description='" + description + '\'' +
                ", authors='" + authors + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}

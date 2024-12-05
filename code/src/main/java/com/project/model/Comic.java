package com.project.model;

import java.util.ArrayList;
import java.util.List;

public class Comic {
    private int id;
    private String name;
    private String description;
    private String coverImageUrl;
    private String publisherName;
    private int issueCount;
    private String dateAdded;
    private String dateLastUpdated;
    private String firstIssue;
    private String lastIssue;
    private String startYear;
    private List<Hero> heroes;
    private List<String> teams;
    private String deck;
    private String rating;

    public Comic() {
        this.heroes = new ArrayList<>();
        this.teams = new ArrayList<>();
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
    
    public String getTeamsAsString() {
        if (teams == null || teams.isEmpty()) {
            return "No teams available";
        }
        return String.join(", ", teams);
    }
    // Getters and setters
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

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName != null ? publisherName : "Unknown Publisher";
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(String dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public String getFirstIssue() {
        return firstIssue;
    }

    public void setFirstIssue(String firstIssue) {
        this.firstIssue = firstIssue;
    }

    public String getLastIssue() {
        return lastIssue;
    }

    public void setLastIssue(String lastIssue) {
        this.lastIssue = lastIssue;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Hero> heroes) {
        this.heroes = heroes != null ? heroes : new ArrayList<>();
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams != null ? teams : new ArrayList<>();
    }

    public String getDeck() {
        return deck == null ? "" : deck;
    }

    public void setDeck(String deck) {
        this.deck = cleanDescription(deck);
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating != null ? rating : "N/A";
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

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", description='" + description + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", issueCount=" + issueCount +
                ", dateAdded='" + dateAdded + '\'' +
                ", dateLastUpdated='" + dateLastUpdated + '\'' +
                ", firstIssue='" + firstIssue + '\'' +
                ", lastIssue='" + lastIssue + '\'' +
                ", startYear='" + startYear + '\'' +
                ", heroes=" + heroes +
                ", teams=" + teams +
                ", deck='" + deck + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
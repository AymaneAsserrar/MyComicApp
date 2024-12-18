package com.project.model;

public class Hero {
    private int id; // Unique identifier for the character
    private String name; // Character's name (e.g., "Batman")
    private String realName; // Real name of the character (e.g., "Bruce Wayne")
    private String imageUrl; // URL of the character's image
    private String description; // Description of the character
    private String[] titles;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", realName='" + realName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    public String[] getTitles() {
        return titles;
    }
    public void setTitles(String[] titles) {
        this.titles = titles;
    }
}

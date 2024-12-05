package com.project.model;

public class User {
    private int id;
    private String email;
    private String passwordHash;
    private String createdAt;
    private int idBiblio;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getIdBiblio() {
        return idBiblio;
    }

    public void setIdBiblio(int idBiblio) {
        this.idBiblio = idBiblio;
    }
}
package com.project.model;

import java.sql.Timestamp;

public class User {
    private static int GLOBAL_BIBLIO_COUNTER = 0;
    private int id;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;
    private int idBiblio;

    public User() {
        synchronized(User.class) {
            GLOBAL_BIBLIO_COUNTER++;
            this.idBiblio = GLOBAL_BIBLIO_COUNTER;
        }
    }

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getIdBiblio() {
        return idBiblio;
    }

    public void setIdBiblio(int idBiblio) {
        // Cette méthode ne devrait plus être utilisée directement
        // L'idBiblio est maintenant géré par le constructeur
        throw new UnsupportedOperationException("IdBiblio cannot be set manually");
    }
}
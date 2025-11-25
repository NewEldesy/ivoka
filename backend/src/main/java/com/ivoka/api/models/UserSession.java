package com.ivoka.api.models;

import java.time.LocalDateTime;

public class UserSession {
    private int id;
    private int userId;
    private String sessionToken;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public UserSession() {
    }

    public UserSession(int id, int userId, String sessionToken, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Méthodes utilitaires
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isExpired();
    }
}
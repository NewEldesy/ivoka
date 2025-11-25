package com.ivoka.api.models;

import java.time.LocalDateTime;
import java.util.List;

public class Cart {
    private int id;
    private Integer userId;
    private String sessionId;
    private List<CartItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart() {
    }

    public Cart(int id, Integer userId, String sessionId) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Méthodes utilitaires
    public double getTotalAmount() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                   .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice().doubleValue())
                   .sum();
    }

    public int getTotalItems() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream()
                   .mapToInt(CartItem::getQuantity)
                   .sum();
    }
}
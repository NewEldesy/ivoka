package com.ivoka.api.dao;

import com.ivoka.api.models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    
    public boolean createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (name, email, subject, message) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, message.getName());
            stmt.setString(2, message.getEmail());
            stmt.setString(3, message.getSubject());
            stmt.setString(4, message.getMessage());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Message message = mapResultSetToMessage(rs);
                messages.add(message);
            }
        }
        
        return messages;
    }
    
    public Message getMessageById(long id) throws SQLException {
        String sql = "SELECT * FROM messages WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMessage(rs);
                }
            }
        }
        
        return null;
    }
    
    public boolean markAsRead(int id) throws SQLException {
        String sql = "UPDATE messages SET `read` = 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateMessage(Message message) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE messages SET");
        boolean hasUpdates = false;
        
        // Dynamiquement construire la requête selon les champs à mettre à jour
        if (message.getSubject() != null && !message.getSubject().isEmpty()) {
            sql.append(" subject = ?");
            hasUpdates = true;
        }
        
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            if (hasUpdates) sql.append(",");
            sql.append(" message = ?");
            hasUpdates = true;
        }
        
        if (hasUpdates) sql.append(",");
        sql.append(" `read` = ?");
        sql.append(" WHERE id = ?");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (message.getSubject() != null && !message.getSubject().isEmpty()) {
                stmt.setString(paramIndex++, message.getSubject());
            }
            
            if (message.getMessage() != null && !message.getMessage().isEmpty()) {
                stmt.setString(paramIndex++, message.getMessage());
            }
            
            stmt.setBoolean(paramIndex++, message.isRead());
            stmt.setInt(paramIndex, message.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteMessage(long id) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setName(rs.getString("name"));
        message.setEmail(rs.getString("email"));
        message.setSubject(rs.getString("subject"));
        message.setMessage(rs.getString("message"));
        message.setRead(rs.getBoolean("read"));
        
        if (rs.getTimestamp("created_at") != null) {
            message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        return message;
    }
}
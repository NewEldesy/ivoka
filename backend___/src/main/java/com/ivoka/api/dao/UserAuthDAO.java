package com.ivoka.api.dao;

import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserAuthDAO {
    
    public boolean createUser(UserAuth user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, password_hash, phone, role, newsletter) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getRole());
            stmt.setBoolean(7, user.isNewsletter());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    public UserAuth getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserAuth(rs);
                }
            }
        }
        
        return null;
    }
    
    public UserAuth getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserAuth(rs);
                }
            }
        }
        
        return null;
    }
    
    public List<UserAuth> getAllUsers() throws SQLException {
        List<UserAuth> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                UserAuth user = mapResultSetToUserAuth(rs);
                users.add(user);
            }
        }
        
        return users;
    }
    
    public boolean updateUser(UserAuth user) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, role = ?, newsletter = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRole());
            stmt.setBoolean(6, user.isNewsletter());
            stmt.setInt(7, user.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean userExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    public UserSession createSession(int userId) throws SQLException {
        String sessionToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        
        String sql = "INSERT INTO user_sessions (user_id, session_token, expires_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, sessionToken);
            stmt.setTimestamp(3, Timestamp.valueOf(expiresAt));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        UserSession session = new UserSession();
                        session.setId(generatedKeys.getInt(1));
                        session.setUserId(userId);
                        session.setSessionToken(sessionToken);
                        session.setExpiresAt(expiresAt);
                        return session;
                    }
                }
            }
        }
        
        return null;
    }
    
    public UserSession validateSession(String sessionToken) throws SQLException {
        String sql = "SELECT * FROM user_sessions WHERE session_token = ? AND expires_at > NOW()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sessionToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserSession session = new UserSession();
                    session.setId(rs.getInt("id"));
                    session.setUserId(rs.getInt("user_id"));
                    session.setSessionToken(rs.getString("session_token"));
                    session.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                    return session;
                }
            }
        }
        
        return null;
    }
    
    public boolean deleteSession(String sessionToken) throws SQLException {
        String sql = "DELETE FROM user_sessions WHERE session_token = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sessionToken);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public int cleanupExpiredSessions() throws SQLException {
        String sql = "DELETE FROM user_sessions WHERE expires_at < NOW()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return stmt.executeUpdate();
        }
    }
    
    private UserAuth mapResultSetToUserAuth(ResultSet rs) throws SQLException {
        UserAuth user = new UserAuth();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setNewsletter(rs.getBoolean("newsletter"));
        
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        return user;
    }
}

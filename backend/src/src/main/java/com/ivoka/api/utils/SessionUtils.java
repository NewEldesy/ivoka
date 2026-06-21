package com.ivoka.api.utils;

import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;

import java.sql.SQLException;

public class SessionUtils {
    
    private static UserAuthDAO userAuthDAO = new UserAuthDAO();
    
    /**
     * Valide une session utilisateur
     * @param sessionToken Le token de session
     * @return La session si valide, null sinon
     */
    public static UserSession validateSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }
        
        try {
            UserSession session = userAuthDAO.validateSession(sessionToken);
            
            if (session != null && session.isValid()) {
                return session;
            }
            
            // Nettoyer la session expirée
            if (session != null) {
                userAuthDAO.deleteSession(sessionToken);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupère l'utilisateur associé à une session
     * @param sessionToken Le token de session
     * @return L'utilisateur si la session est valide, null sinon
     */
    public static UserAuth getUserFromSession(String sessionToken) {
        UserSession session = validateSession(sessionToken);
        
        if (session != null) {
            try {
                return userAuthDAO.getUserById(session.getUserId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * Vérifie si un utilisateur est administrateur
     * @param sessionToken Le token de session
     * @return true si l'utilisateur est admin et la session est valide
     */
    public static boolean isAdmin(String sessionToken) {
        UserAuth user = getUserFromSession(sessionToken);
        return user != null && "admin".equals(user.getRole());
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     * @param sessionToken Le token de session
     * @return true si l'utilisateur est connecté
     */
    public static boolean isAuthenticated(String sessionToken) {
        return validateSession(sessionToken) != null;
    }
    
    /**
     * Nettoie les sessions expirées
     */
    public static void cleanupExpiredSessions() {
        try {
            userAuthDAO.cleanupExpiredSessions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
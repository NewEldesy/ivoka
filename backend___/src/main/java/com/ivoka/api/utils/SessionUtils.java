package com.ivoka.api.utils;

import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class SessionUtils {

    private static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);
    private static UserAuthDAO userAuthDAO = new UserAuthDAO();

    public static UserSession validateSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }

        try {
            UserSession session = userAuthDAO.validateSession(sessionToken);

            if (session != null && session.isValid()) {
                return session;
            }

            if (session != null) {
                userAuthDAO.deleteSession(sessionToken);
            }

        } catch (SQLException e) {
            logger.warn("Error validating session", e);
        }

        return null;
    }

    public static UserAuth getUserFromSession(String sessionToken) {
        UserSession session = validateSession(sessionToken);

        if (session != null) {
            try {
                return userAuthDAO.getUserById(session.getUserId());
            } catch (SQLException e) {
                logger.warn("Error fetching user by session", e);
            }
        }

        return null;
    }

    public static boolean isAdmin(String sessionToken) {
        UserAuth user = getUserFromSession(sessionToken);
        return user != null && "admin".equals(user.getRole());
    }

    public static boolean isAuthenticated(String sessionToken) {
        return validateSession(sessionToken) != null;
    }

    public static void cleanupExpiredSessions() {
        try {
            userAuthDAO.cleanupExpiredSessions();
        } catch (SQLException e) {
            logger.warn("Error cleaning up expired sessions", e);
        }
    }
}

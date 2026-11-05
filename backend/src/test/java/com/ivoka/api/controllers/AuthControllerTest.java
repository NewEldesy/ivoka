package com.ivoka.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ivoka.api.config.DataSourceConfig;
import com.ivoka.api.config.DatabaseConnectionInitializer;
import com.ivoka.api.config.DataInitializer;
import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    value = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { DataSourceConfig.class, DatabaseConnectionInitializer.class, DataInitializer.class }
    )
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // UserAuthDAO est instancié directement dans le contrôleur (new UserAuthDAO())
    // → on injecte un mock via le context Spring pour le remplacer dans les tests
    @MockBean
    private UserAuthDAO userAuthDAO;

    // --- login ---

    @Test
    void login_withValidCredentials_returns200WithToken() throws Exception {
        UserAuth user = buildUser(1, "user@ivoka.fr", "customer");
        UserSession session = buildSession(user.getId());

        when(userAuthDAO.getUserByEmail("user@ivoka.fr")).thenReturn(user);
        when(userAuthDAO.createSession(1)).thenReturn(session);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "login",
                        "email", "user@ivoka.fr",
                        "password", "User@ivoka2024"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.sessionToken").value("test-token-123"));
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        UserAuth user = buildUser(1, "user@ivoka.fr", "customer");
        when(userAuthDAO.getUserByEmail("user@ivoka.fr")).thenReturn(user);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "login",
                        "email", "user@ivoka.fr",
                        "password", "wrongpassword"
                ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownEmail_returns401() throws Exception {
        when(userAuthDAO.getUserByEmail(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "login",
                        "email", "unknown@ivoka.fr",
                        "password", "SomePass1!"
                ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withEmptyFields_returns400() throws Exception {
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("action", "login"))))
                .andExpect(status().isBadRequest());
    }

    // --- register ---

    @Test
    void register_withWeakPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "register",
                        "firstName", "Jean",
                        "lastName", "Dupont",
                        "email", "jean@example.com",
                        "password", "weak"
                ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void register_withExistingEmail_returns409() throws Exception {
        when(userAuthDAO.userExists("jean@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "register",
                        "firstName", "Jean",
                        "lastName", "Dupont",
                        "email", "jean@example.com",
                        "password", "StrongPass1!"
                ))))
                .andExpect(status().isConflict());
    }

    @Test
    void register_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "register",
                        "firstName", "Jean",
                        "lastName", "Dupont",
                        "email", "not-an-email",
                        "password", "StrongPass1!"
                ))))
                .andExpect(status().isBadRequest());
    }

    // --- logout ---

    @Test
    void logout_withValidToken_returns200() throws Exception {
        when(userAuthDAO.deleteSession("valid-token")).thenReturn(true);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "logout",
                        "sessionToken", "valid-token"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void logout_withUnknownToken_returns400() throws Exception {
        when(userAuthDAO.deleteSession(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "action", "logout",
                        "sessionToken", "unknown-token"
                ))))
                .andExpect(status().isBadRequest());
    }

    // --- action inconnue ---

    @Test
    void unknownAction_returns400() throws Exception {
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("action", "hack"))))
                .andExpect(status().isBadRequest());
    }

    // --- helpers ---

    private UserAuth buildUser(int id, String email, String role) {
        UserAuth u = new UserAuth();
        u.setId(id);
        u.setFirstName("Test");
        u.setLastName("User");
        u.setEmail(email);
        // Hash réel de "User@ivoka2024" pour que verifyPassword fonctionne en test
        u.setPasswordHash(com.ivoka.api.utils.PasswordUtils.hashPassword("User@ivoka2024"));
        u.setRole(role);
        return u;
    }

    private UserSession buildSession(int userId) {
        UserSession s = new UserSession();
        s.setId(1);
        s.setUserId(userId);
        s.setSessionToken("test-token-123");
        s.setExpiresAt(LocalDateTime.now().plusHours(24));
        return s;
    }
}

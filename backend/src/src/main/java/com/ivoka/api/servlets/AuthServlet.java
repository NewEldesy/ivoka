package com.ivoka.api.servlets;

import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;
import com.ivoka.api.utils.PasswordUtils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {
    private UserAuthDAO userAuthDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userAuthDAO = new UserAuthDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            JsonObject jsonInput = Json.createReader(request.getReader()).readObject();
            String action = jsonInput.getString("action", "");
            
            switch (action) {
                case "register":
                    handleRegister(jsonInput, out, response);
                    break;
                case "login":
                    handleLogin(jsonInput, out, response);
                    break;
                case "logout":
                    handleLogout(jsonInput, out, response);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Action non reconnue")
                            .build();
                    out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors du traitement de la requête")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    private void handleRegister(JsonObject jsonInput, PrintWriter out, HttpServletResponse response) throws SQLException {
        // Validation des champs requis
        String firstName = jsonInput.getString("firstName", "").trim();
        String lastName = jsonInput.getString("lastName", "").trim();
        String email = jsonInput.getString("email", "").trim();
        String password = jsonInput.getString("password", "");
        String phone = jsonInput.getString("phone", "").trim();
        boolean newsletter = jsonInput.getBoolean("newsletter", false);
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Tous les champs obligatoires doivent être remplis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Validation de l'email
        if (!isValidEmail(email)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Adresse email invalide")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Validation du mot de passe
        if (password.length() < 8) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Le mot de passe doit contenir au moins 8 caractères")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Vérifier si l'utilisateur existe déjà
        if (userAuthDAO.userExists(email)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Un utilisateur avec cet email existe déjà")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Hacher le mot de passe
        String passwordHash = PasswordUtils.hashPassword(password);
        
        UserAuth user = new UserAuth();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setPhone(phone);
        user.setRole("customer");
        user.setNewsletter(newsletter);
        
        boolean created = userAuthDAO.createUser(user);
        
        if (created) {
            // Créer une session pour l'utilisateur
            UserSession session = userAuthDAO.createSession(user.getId());
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Compte créé avec succès")
                    .add("user", Json.createObjectBuilder()
                            .add("id", user.getId())
                            .add("firstName", user.getFirstName())
                            .add("lastName", user.getLastName())
                            .add("email", user.getEmail())
                            .add("role", user.getRole())
                            .add("newsletter", user.isNewsletter()))
                    .add("sessionToken", session != null ? session.getSessionToken() : null)
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la création du compte")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleLogin(JsonObject jsonInput, PrintWriter out, HttpServletResponse response) throws SQLException {
        String email = jsonInput.getString("email", "").trim();
        String password = jsonInput.getString("password", "");
        
        if (email.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Email et mot de passe requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Récupérer l'utilisateur par email
        UserAuth user = userAuthDAO.getUserByEmail(email);
        
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Email ou mot de passe incorrect")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Vérifier le mot de passe
        if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Email ou mot de passe incorrect")
                    .build();
            out.print(error.toString());
            return;
        }
        
        // Créer une session
        UserSession session = userAuthDAO.createSession(user.getId());
        
        if (session != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Connexion réussie")
                    .add("user", Json.createObjectBuilder()
                            .add("id", user.getId())
                            .add("firstName", user.getFirstName())
                            .add("lastName", user.getLastName())
                            .add("email", user.getEmail())
                            .add("role", user.getRole())
                            .add("newsletter", user.isNewsletter()))
                    .add("sessionToken", session.getSessionToken())
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la création de la session")
                    .build();
            out.print(error.toString());
        }
    }

    private void handleLogout(JsonObject jsonInput, PrintWriter out, HttpServletResponse response) throws SQLException {
        String sessionToken = jsonInput.getString("sessionToken", "");
        
        if (sessionToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Token de session requis")
                    .build();
            out.print(error.toString());
            return;
        }
        
        boolean deleted = userAuthDAO.deleteSession(sessionToken);
        
        if (deleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject success = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Déconnexion réussie")
                    .build();
            out.print(success.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Session introuvable")
                    .build();
            out.print(error.toString());
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
package com.ivoka.api.servlets;

import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;
import com.ivoka.api.utils.PasswordUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {
    private UserAuthDAO userAuthDAO;
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);

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
            logger.error("Error processing auth request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors du traitement de la requête")
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    private void handleRegister(JsonObject jsonInput, PrintWriter out, HttpServletResponse response) throws SQLException {
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

        if (!isValidEmail(email)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Adresse email invalide")
                    .build();
            out.print(error.toString());
            return;
        }

        if (!PasswordUtils.isPasswordStrong(password)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Le mot de passe n'est pas suffisamment fort")
                    .build();
            out.print(error.toString());
            return;
        }

        if (userAuthDAO.userExists(email)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Un utilisateur avec cet email existe déjà")
                    .build();
            out.print(error.toString());
            return;
        }

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
                    .add("sessionToken", session != null ? session.getSessionToken() : JsonObject.NULL)
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

        UserAuth user = userAuthDAO.getUserByEmail(email);

        if (user == null || !PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Email ou mot de passe incorrect")
                    .build();
            out.print(error.toString());
            return;
        }

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

}

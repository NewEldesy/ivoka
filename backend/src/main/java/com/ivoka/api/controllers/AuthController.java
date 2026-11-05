package com.ivoka.api.controllers;

import com.ivoka.api.dao.UserAuthDAO;
import com.ivoka.api.models.UserAuth;
import com.ivoka.api.models.UserSession;
import com.ivoka.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserAuthDAO userAuthDAO;

    public AuthController() {
        this.userAuthDAO = new UserAuthDAO();
    }

    AuthController(UserAuthDAO userAuthDAO) {
        this.userAuthDAO = userAuthDAO;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> handlePost(@RequestBody Map<String, Object> body) {
        String action = (String) body.getOrDefault("action", "");
        try {
            return switch (action) {
                case "register" -> handleRegister(body);
                case "login"    -> handleLogin(body);
                case "logout"   -> handleLogout(body);
                default -> error(HttpStatus.BAD_REQUEST, "Action non reconnue");
            };
        } catch (Exception e) {
            logger.error("Erreur AuthController: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du traitement de la requête");
        }
    }

    private ResponseEntity<Map<String, Object>> handleRegister(Map<String, Object> body) throws SQLException {
        String firstName = trim(body, "firstName");
        String lastName  = trim(body, "lastName");
        String email     = trim(body, "email");
        String password  = str(body, "password");
        String phone     = trim(body, "phone");
        boolean newsletter = bool(body, "newsletter");

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty())
            return error(HttpStatus.BAD_REQUEST, "Tous les champs obligatoires doivent être remplis");
        if (!isValidEmail(email))
            return error(HttpStatus.BAD_REQUEST, "Adresse email invalide");
        if (!PasswordUtils.isPasswordStrong(password))
            return error(HttpStatus.BAD_REQUEST, "Le mot de passe doit contenir au moins 8 caractères avec majuscule, minuscule et chiffre");
        if (userAuthDAO.userExists(email))
            return error(HttpStatus.CONFLICT, "Un utilisateur avec cet email existe déjà");

        UserAuth user = new UserAuth();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtils.hashPassword(password));
        user.setPhone(phone);
        user.setRole("customer");
        user.setNewsletter(newsletter);

        if (!userAuthDAO.createUser(user))
            return error(HttpStatus.BAD_REQUEST, "Erreur lors de la création du compte");

        UserSession session = userAuthDAO.createSession(user.getId());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", true);
        resp.put("message", "Compte créé avec succès");
        resp.put("user", userMap(user));
        resp.put("sessionToken", session != null ? session.getSessionToken() : null);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    private ResponseEntity<Map<String, Object>> handleLogin(Map<String, Object> body) throws SQLException {
        String email    = trim(body, "email");
        String password = str(body, "password");

        if (email.isEmpty() || password.isEmpty())
            return error(HttpStatus.BAD_REQUEST, "Email et mot de passe requis");

        UserAuth user = userAuthDAO.getUserByEmail(email);
        if (user == null || !PasswordUtils.verifyPassword(password, user.getPasswordHash()))
            return error(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");

        UserSession session = userAuthDAO.createSession(user.getId());
        if (session == null)
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la session");

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", true);
        resp.put("message", "Connexion réussie");
        resp.put("user", userMap(user));
        resp.put("sessionToken", session.getSessionToken());
        return ResponseEntity.ok(resp);
    }

    private ResponseEntity<Map<String, Object>> handleLogout(Map<String, Object> body) throws SQLException {
        String token = str(body, "sessionToken");
        if (token.isEmpty())
            return error(HttpStatus.BAD_REQUEST, "Token de session requis");
        if (!userAuthDAO.deleteSession(token))
            return error(HttpStatus.BAD_REQUEST, "Session introuvable");
        return ResponseEntity.ok(Map.of("success", true, "message", "Déconnexion réussie"));
    }

    private Map<String, Object> userMap(UserAuth u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("firstName", u.getFirstName());
        m.put("lastName", u.getLastName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole());
        m.put("newsletter", u.isNewsletter());
        return m;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String str(Map<String, Object> m, String k)  { return m.getOrDefault(k, "").toString(); }
    private String trim(Map<String, Object> m, String k) { return str(m, k).trim(); }
    private boolean bool(Map<String, Object> m, String k) { Object v = m.get(k); return v instanceof Boolean b && b; }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }
}

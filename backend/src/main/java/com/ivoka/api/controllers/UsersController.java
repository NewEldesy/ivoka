package com.ivoka.api.controllers;

import com.ivoka.api.dao.UserDAO;
import com.ivoka.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserDAO userDAO = new UserDAO();

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        try {
            String firstName = trim(body, "firstName");
            String lastName  = trim(body, "lastName");
            String email     = trim(body, "email");
            String phone     = trim(body, "phone");
            boolean newsletter = boolDef(body, "newsletter");

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty())
                return error(HttpStatus.BAD_REQUEST, "Les champs prénom, nom et email sont obligatoires");
            if (!isValidEmail(email))
                return error(HttpStatus.BAD_REQUEST, "Adresse email invalide");
            if (userDAO.userExists(email))
                return error(HttpStatus.CONFLICT, "Un utilisateur avec cet email existe déjà");

            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setNewsletter(newsletter);

            if (!userDAO.createUser(user))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la création de l'utilisateur");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Utilisateur créé avec succès",
                                 "id", user.getId(), "newsletter", user.isNewsletter()));
        } catch (Exception e) {
            logger.error("Erreur createUser: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'utilisateur");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String trim(Map<String, Object> m, String k) { return m.getOrDefault(k, "").toString().trim(); }
    private boolean boolDef(Map<String, Object> m, String k) {
        Object v = m.get(k); return v instanceof Boolean b && b;
    }
    private ResponseEntity<?> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("error", msg));
    }
}

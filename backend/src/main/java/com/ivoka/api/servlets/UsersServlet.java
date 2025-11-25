package com.ivoka.api.servlets;

import com.ivoka.api.dao.UserDAO;
import com.ivoka.api.models.User;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/users")
public class UsersServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
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
            
            // Validation des champs requis
            String firstName = jsonInput.getString("firstName", "").trim();
            String lastName = jsonInput.getString("lastName", "").trim();
            String email = jsonInput.getString("email", "").trim();
            String phone = jsonInput.getString("phone", "").trim();
            boolean newsletter = jsonInput.getBoolean("newsletter", false);
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Les champs prénom, nom et email sont obligatoires")
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
            
            // Vérifier si l'utilisateur existe déjà
            if (userDAO.userExists(email)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Un utilisateur avec cet email existe déjà")
                        .build();
                out.print(error.toString());
                return;
            }
            
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setNewsletter(newsletter);
            
            boolean created = userDAO.createUser(user);
            
            if (created) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                JsonObject success = Json.createObjectBuilder()
                        .add("success", true)
                        .add("message", "Utilisateur créé avec succès")
                        .add("id", user.getId())
                        .add("newsletter", user.isNewsletter())
                        .build();
                out.print(success.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Erreur lors de la création de l'utilisateur")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la création de l'utilisateur")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isValidEmail(String email) {
        // Validation simple de l'email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
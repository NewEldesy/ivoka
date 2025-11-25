package com.ivoka.api.servlets;

import com.ivoka.api.dao.MessageDAO;
import com.ivoka.api.models.Message;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/messages")
public class MessagesServlet extends HttpServlet {
    private MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        messageDAO = new MessageDAO();
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
            String name = jsonInput.getString("name", "").trim();
            String email = jsonInput.getString("email", "").trim();
            String subject = jsonInput.getString("subject", "").trim();
            String messageText = jsonInput.getString("message", "").trim();
            
            if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || messageText.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Tous les champs sont obligatoires")
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
            
            Message message = new Message();
            message.setName(name);
            message.setEmail(email);
            message.setSubject(subject);
            message.setMessage(messageText);
            
            boolean created = messageDAO.createMessage(message);
            
            if (created) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                JsonObject success = Json.createObjectBuilder()
                        .add("success", true)
                        .add("message", "Votre message a été envoyé avec succès")
                        .add("id", message.getId())
                        .build();
                out.print(success.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Erreur lors de l'envoi du message")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de l'envoi du message")
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
package com.ivoka.api.servlets;

import com.ivoka.api.dao.MessageDAO;
import com.ivoka.api.models.Message;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/messages/*")
public class MessagesServlet extends HttpServlet {
    private MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        messageDAO = new MessageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all messages
                List<Message> messages = messageDAO.getAllMessages();
                
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Message msg : messages) {
                    JsonObject jsonMsg = Json.createObjectBuilder()
                            .add("id", msg.getId())
                            .add("name", msg.getName())
                            .add("senderEmail", msg.getEmail())
                            .add("subject", msg.getSubject())
                            .add("message", msg.getMessage())
                            .add("read", msg.isRead())
                            .add("createdAt", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : "")
                            .build();
                    arrayBuilder.add(jsonMsg);
                }
                
                out.print(arrayBuilder.build().toString());
            } else {
                // Get specific message
                try {
                    long messageId = Long.parseLong(pathInfo.substring(1));
                    Message message = messageDAO.getMessageById(messageId);
                    
                    if (message != null) {
                        JsonObject jsonMsg = Json.createObjectBuilder()
                                .add("id", message.getId())
                                .add("name", message.getName())
                                .add("senderEmail", message.getEmail())
                                .add("subject", message.getSubject())
                                .add("message", message.getMessage())
                                .add("read", message.isRead())
                                .add("createdAt", message.getCreatedAt() != null ? message.getCreatedAt().toString() : "")
                                .build();
                        out.print(jsonMsg.toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JsonObject error = Json.createObjectBuilder()
                                .add("error", "Message non trouvé")
                                .build();
                        out.print(error.toString());
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "ID du message invalide")
                            .build();
                    out.print(error.toString());
                }
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la récupération des messages")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "ID du message requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            try {
                long messageId = Long.parseLong(pathInfo.substring(1));
                JsonObject jsonInput = Json.createReader(request.getReader()).readObject();
                
                Message message = messageDAO.getMessageById(messageId);
                
                if (message == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Message non trouvé")
                            .build();
                    out.print(error.toString());
                    return;
                }
                
                // Update fields if provided
                if (jsonInput.containsKey("read")) {
                    message.setRead(jsonInput.getBoolean("read"));
                }
                if (jsonInput.containsKey("subject")) {
                    message.setSubject(jsonInput.getString("subject"));
                }
                if (jsonInput.containsKey("message")) {
                    message.setMessage(jsonInput.getString("message"));
                }
                
                boolean updated = messageDAO.updateMessage(message);
                
                if (updated) {
                    JsonObject success = Json.createObjectBuilder()
                            .add("success", true)
                            .add("message", "Message mis à jour avec succès")
                            .build();
                    out.print(success.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Erreur lors de la mise à jour du message")
                            .build();
                    out.print(error.toString());
                }
                
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "ID du message invalide")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la mise à jour du message")
                    .add("message", e.getMessage())
                    .build();
            out.print(error.toString());
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "ID du message requis")
                        .build();
                out.print(error.toString());
                return;
            }
            
            try {
                long messageId = Long.parseLong(pathInfo.substring(1));
                
                boolean deleted = messageDAO.deleteMessage(messageId);
                
                if (deleted) {
                    JsonObject success = Json.createObjectBuilder()
                            .add("success", true)
                            .add("message", "Message supprimé avec succès")
                            .build();
                    out.print(success.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = Json.createObjectBuilder()
                            .add("error", "Message non trouvé")
                            .build();
                    out.print(error.toString());
                }
                
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "ID du message invalide")
                        .build();
                out.print(error.toString());
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = Json.createObjectBuilder()
                    .add("error", "Erreur lors de la suppression du message")
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
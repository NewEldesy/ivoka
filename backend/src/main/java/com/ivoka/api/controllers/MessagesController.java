package com.ivoka.api.controllers;

import com.ivoka.api.dao.MessageDAO;
import com.ivoka.api.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    private static final Logger logger = LoggerFactory.getLogger(MessagesController.class);
    private final MessageDAO messageDAO = new MessageDAO();

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Message> messages = messageDAO.getAllMessages();
            return ResponseEntity.ok(messages.stream().map(this::toMap).toList());
        } catch (Exception e) {
            logger.error("Erreur getMessages: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des messages");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable long id) {
        try {
            Message msg = messageDAO.getMessageById(id);
            if (msg == null) return error(HttpStatus.NOT_FOUND, "Message non trouvé");
            return ResponseEntity.ok(toMap(msg));
        } catch (Exception e) {
            logger.error("Erreur getMessage: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du message");
        }
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Map<String, Object> body) {
        try {
            String name        = trim(body, "name");
            String email       = trim(body, "email");
            String subject     = trim(body, "subject");
            String messageText = trim(body, "message");

            if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || messageText.isEmpty())
                return error(HttpStatus.BAD_REQUEST, "Tous les champs sont obligatoires");
            if (!isValidEmail(email))
                return error(HttpStatus.BAD_REQUEST, "Adresse email invalide");

            Message msg = new Message();
            msg.setName(name);
            msg.setEmail(email);
            msg.setSubject(subject);
            msg.setMessage(messageText);

            if (!messageDAO.createMessage(msg))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de l'envoi du message");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Votre message a été envoyé avec succès", "id", msg.getId()));
        } catch (Exception e) {
            logger.error("Erreur createMessage: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'envoi du message");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable long id, @RequestBody Map<String, Object> body) {
        try {
            Message msg = messageDAO.getMessageById(id);
            if (msg == null) return error(HttpStatus.NOT_FOUND, "Message non trouvé");

            if (body.containsKey("read"))    msg.setRead((Boolean) body.get("read"));
            if (body.containsKey("subject")) msg.setSubject(body.get("subject").toString());
            if (body.containsKey("message")) msg.setMessage(body.get("message").toString());

            if (!messageDAO.updateMessage(msg))
                return error(HttpStatus.BAD_REQUEST, "Erreur lors de la mise à jour du message");
            return ResponseEntity.ok(Map.of("success", true, "message", "Message mis à jour avec succès"));
        } catch (Exception e) {
            logger.error("Erreur updateMessage: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du message");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable long id) {
        try {
            if (!messageDAO.deleteMessage(id))
                return error(HttpStatus.NOT_FOUND, "Message non trouvé");
            return ResponseEntity.ok(Map.of("success", true, "message", "Message supprimé avec succès"));
        } catch (Exception e) {
            logger.error("Erreur deleteMessage: {}", e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression du message");
        }
    }

    private Map<String, Object> toMap(Message msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", msg.getId());
        m.put("name", msg.getName());
        m.put("senderEmail", msg.getEmail());
        m.put("subject", msg.getSubject());
        m.put("message", msg.getMessage());
        m.put("read", msg.isRead());
        m.put("createdAt", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : "");
        return m;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String trim(Map<String, Object> m, String k) {
        return m.getOrDefault(k, "").toString().trim();
    }
    private ResponseEntity<?> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("error", msg));
    }
}

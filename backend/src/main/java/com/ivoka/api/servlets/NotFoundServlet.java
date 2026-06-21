package com.ivoka.api.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet handling 404 errors for non-existent API routes
 * Returns a JSON response for API clients
 */
@WebServlet(name = "NotFoundServlet", value = "/api/*")
public class NotFoundServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Log the not found request
        System.out.println("404 Error - Path not found: " + requestPath + " [" + method + "]");

        // Set response status
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create JSON response (using StringBuilder for simplicity)
        long timestamp = System.currentTimeMillis();
        String jsonResponse = buildJsonResponse(requestPath, method, timestamp);

        // Send response
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    /**
     * Builds a JSON response string for 404 errors
     */
    private String buildJsonResponse(String path, String method, long timestamp) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"error\":true,");
        json.append("\"status\":404,");
        json.append("\"message\":\"Ressource non trouvée\",");
        json.append("\"details\":\"L'endpoint '").append(escapeJson(path)).append("' n'existe pas\",");
        json.append("\"method\":\"").append(method).append("\",");
        json.append("\"path\":\"").append(escapeJson(path)).append("\",");
        json.append("\"timestamp\":").append(timestamp);
        json.append("}");
        return json.toString();
    }

    /**
     * Escapes special JSON characters in a string
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}

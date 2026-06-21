package com.ivoka.api.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;

@WebServlet("/resources/*")
public class ResourceServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }
        
        // Servir le fichier depuis le répertoire resources de l'application
        InputStream resourceStream = getServletContext().getResourceAsStream("/resources" + path);
        
        if (resourceStream != null) {
            // Déterminer le type MIME
            String filename = path.substring(path.lastIndexOf("/") + 1);
            if (filename.endsWith(".png")) {
                response.setContentType("image/png");
            } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                response.setContentType("image/jpeg");
            } else if (filename.endsWith(".gif")) {
                response.setContentType("image/gif");
            }
            
            // Copier le contenu du fichier dans la réponse
            byte[] buffer = new byte[1024];
            int bytesRead;
            OutputStream out = response.getOutputStream();
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            resourceStream.close();
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

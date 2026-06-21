package com.ivoka.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS Filter - Autorise les requêtes du Frontend React
 * 
 * Permet à l'interface React (même domaine différent port) d'accéder à l'API
 */
@WebFilter(urlPatterns = {"/api/*"})
public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Autoriser toutes les origines (À personnaliser en production)
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        
        // Méthodes HTTP autorisées
        httpResponse.setHeader("Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        
        // En-têtes autorisés
        httpResponse.setHeader("Access-Control-Allow-Headers", 
            "Content-Type, Authorization, Accept, Origin, X-Requested-With");
        
        // Permettre les cookies
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        // Durée du cache des préflights
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Répondre aux requêtes OPTIONS (preflight)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Continuer la chaîne de traitement
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre
    }

    @Override
    public void destroy() {
        // Nettoyage du filtre
    }
}

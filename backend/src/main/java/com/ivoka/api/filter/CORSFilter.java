package com.ivoka.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter(urlPatterns = {"/api/*"})
public class CORSFilter implements Filter {

    // Origins autorisées : dev React (3000) + production Tomcat (8080)
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
        "http://localhost:3000",
        "http://localhost:8080"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");

        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        }

        httpResponse.setHeader("Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        httpResponse.setHeader("Access-Control-Allow-Headers",
            "Content-Type, Authorization, Accept, Origin, X-Requested-With");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Vary", "Origin");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

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

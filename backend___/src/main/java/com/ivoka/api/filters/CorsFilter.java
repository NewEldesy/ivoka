package com.ivoka.api.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {

    private String allowedOrigin;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Read allowed origin from env var; fallback to wildcard in dev
        allowedOrigin = System.getenv("ALLOWED_ORIGIN");
        if (allowedOrigin == null || allowedOrigin.isEmpty()) {
            allowedOrigin = "*"; // change in production to explicit origin
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        // If allowedOrigin is not wildcard and credentials are used, allow credentials
        if (!"*".equals(allowedOrigin)) {
            res.setHeader("Access-Control-Allow-Credentials", "true");
        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}

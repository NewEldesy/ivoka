package com.ivoka.api.listener;

import com.ivoka.api.dao.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            DatabaseConnection.initialize(context);
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing database connection pool", e);
            throw new RuntimeException("Database initialization error", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            DatabaseConnection.close();
            logger.info("Database connection pool closed");
        } catch (Exception e) {
            logger.warn("Error closing database connection pool", e);
        }
    }
}

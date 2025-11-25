package com.ivoka.api.listener;

import com.ivoka.api.dao.DatabaseConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        try {
            // Initialiser la connexion à la base de données
            DatabaseConnection.initialize(context);
            System.out.println("✅ Connexion à la base de données initialisée avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            throw new RuntimeException("Erreur d'initialisation de la base de données", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            // Fermer le pool de connexions
            DatabaseConnection.close();
            System.out.println("✅ Connexion à la base de données fermée");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion à la base de données: " + e.getMessage());
        }
    }
}
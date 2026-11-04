package com.ivoka.api.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static BasicDataSource dataSource;

    private static Properties loadEnv(ServletContext context) {
        Properties env = new Properties();
        // Cherche .env deux niveaux au-dessus du répertoire de déploiement WAR
        String realPath = context.getRealPath("/");
        if (realPath != null) {
            for (File dir = new File(realPath); dir != null; dir = dir.getParentFile()) {
                File envFile = new File(dir, ".env");
                if (envFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(envFile)) {
                        env.load(fis);
                        logger.info("Fichier .env chargé depuis : " + envFile.getAbsolutePath());
                    } catch (IOException e) {
                        logger.warning("Impossible de lire .env : " + e.getMessage());
                    }
                    break;
                }
            }
        }
        return env;
    }

    public static void initialize(ServletContext context) {
        if (dataSource == null) {
            dataSource = new BasicDataSource();

            Properties env = loadEnv(context);

            String dbUrl = env.getProperty("DB_HOST") != null
                ? "jdbc:mysql://" + env.getProperty("DB_HOST", "localhost")
                    + ":" + env.getProperty("DB_PORT", "3306")
                    + "/" + env.getProperty("DB_NAME", "ivoka_db")
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                : context.getInitParameter("db.url");
            String dbUser     = env.getProperty("DB_USER",     context.getInitParameter("db.user"));
            String dbPassword = env.getProperty("DB_PASSWORD", context.getInitParameter("db.password"));
            String dbDriver   = context.getInitParameter("db.driver");

            dataSource.setDriverClassName(dbDriver);
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(dbPassword);
            
            // Configuration du pool de connexions
            dataSource.setInitialSize(5);
            dataSource.setMaxTotal(20);
            dataSource.setMaxIdle(10);
            dataSource.setMinIdle(5);
            dataSource.setMaxWaitMillis(10000);
            
            // Validation des connexions
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(true);
            dataSource.setTestWhileIdle(true);
            dataSource.setTimeBetweenEvictionRunsMillis(30000);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        return dataSource.getConnection();
    }
    
    public static void close() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
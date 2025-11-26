package com.ivoka.api.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import jakarta.servlet.ServletContext;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static BasicDataSource dataSource;
    
    public static void initialize(ServletContext context) {
        if (dataSource == null) {
            dataSource = new BasicDataSource();
            
            // Configuration de la base de données
            String dbUrl = context.getInitParameter("db.url");
            String dbUser = context.getInitParameter("db.user");
            String dbPassword = context.getInitParameter("db.password");
            String dbDriver = context.getInitParameter("db.driver");
            
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
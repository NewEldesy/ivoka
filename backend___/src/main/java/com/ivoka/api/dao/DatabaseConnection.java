package com.ivoka.api.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static BasicDataSource dataSource;

    public static void initialize(ServletContext context) {
        if (dataSource == null) {
            dataSource = new BasicDataSource();

            // Prefer environment variables (for cloud/container). Fallback to servlet context params.
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            String dbDriver = System.getenv("DB_DRIVER");

            if (dbUrl == null || dbUrl.isEmpty()) {
                dbUrl = context.getInitParameter("db.url");
            }
            if (dbUser == null || dbUser.isEmpty()) {
                dbUser = context.getInitParameter("db.user");
            }
            if (dbPassword == null || dbPassword.isEmpty()) {
                dbPassword = context.getInitParameter("db.password");
            }
            if (dbDriver == null || dbDriver.isEmpty()) {
                dbDriver = context.getInitParameter("db.driver");
            }

            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new IllegalStateException("Database URL is not configured. Set DB_URL env var or context-param db.url");
            }

            logger.info("Initializing DB pool with URL: {}", dbUrl);

            dataSource.setDriverClassName(dbDriver != null ? dbDriver : "com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(dbPassword);

            // Pool configuration
            dataSource.setInitialSize(5);
            dataSource.setMaxTotal(20);
            dataSource.setMaxIdle(10);
            dataSource.setMinIdle(5);
            dataSource.setMaxWaitMillis(10000);

            // Validation
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(true);
            dataSource.setTestWhileIdle(true);
            dataSource.setTimeBetweenEvictionRunsMillis(30000);

            // Abandoned connection handling
            dataSource.setRemoveAbandonedOnBorrow(true);
            dataSource.setRemoveAbandonedTimeout(60);
            dataSource.setLogAbandoned(true);
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
            logger.info("DataSource closed");
        }
    }
}

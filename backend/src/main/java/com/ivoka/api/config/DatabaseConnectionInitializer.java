package com.ivoka.api.config;

import com.ivoka.api.dao.DatabaseConnection;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseConnectionInitializer {

    public DatabaseConnectionInitializer(DataSource dataSource) {
        DatabaseConnection.setDataSource(dataSource);
    }
}

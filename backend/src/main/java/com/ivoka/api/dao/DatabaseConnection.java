package com.ivoka.api.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DataSource dataSource;

    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource Spring non initialisé");
        }
        return dataSource.getConnection();
    }
}

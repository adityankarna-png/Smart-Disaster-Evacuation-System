package com.disaster.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            System.getenv("DISASTER_DB_URL");

    private static final String USER =
            System.getenv("DISASTER_DB_USER");

    private static final String PASSWORD =
            System.getenv("DISASTER_DB_PASSWORD");

    public static Connection getConnection()
            throws SQLException {

        if (URL == null ||
                USER == null ||
                PASSWORD == null) {

            throw new SQLException(
                    "Database environment variables are not configured."
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}
package com.dev.tool;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteConnection {
    private static final String DB_URL = "jdbc:sqlite:database.db";

    public static Connection getConnection() throws Exception {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {

        }
        return connection;
    }
}

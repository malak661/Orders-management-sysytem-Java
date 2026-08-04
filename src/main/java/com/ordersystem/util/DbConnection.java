package com.ordersystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class responsible for providing a JDBC Connection to the SQLite database.
 * TODO: set the DB file path (e.g. "jdbc:sqlite:orders.db")
 * TODO: implement getConnection()
 * TODO: implement initializeSchema() -> run schema.sql on first startup (AC9)
 */
public class DbConnection {

    private static final String DB_URL = "jdbc:sqlite:orders.db"; // TODO confirm path

    public static Connection getConnection() throws SQLException {
        // TODO
        return null;
    }

    public static void initializeSchema() {
        // TODO: read resources/schema.sql and execute it if tables don't exist
    }
}

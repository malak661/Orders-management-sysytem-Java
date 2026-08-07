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
          String schemaSql = readSchema();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            for (String sqlCommand : schemaSql.split(";")) {
                String sql = sqlCommand.trim();
                if (!sql.isEmpty()) {
                    statement.execute(sql);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }
    }
    
}

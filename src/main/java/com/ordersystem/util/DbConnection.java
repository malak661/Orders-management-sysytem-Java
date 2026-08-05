package com.ordersystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DbConnection {

    private static final String DB_URL = "jdbc:sqlite:orders.db";

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initializeSchema() {
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement();
                InputStream inputStream = DbConnection.class
                        .getClassLoader()
                        .getResourceAsStream("schema.sql")
        ) {

            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found.");
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder contents = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("--.*$", "").trim();
                if (!line.isEmpty()) {
                    contents.append(line).append(' ');
                }
            }

            String[] statements = contents.toString().split(";");
            for (String statementText : statements) {
                String sqlStatement = statementText.trim();
                if (sqlStatement.isEmpty()) {
                    continue;
                }
                statement.execute(sqlStatement);
            }

            System.out.println("Database schema initialized successfully.");

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }
}

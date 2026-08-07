package com.ordersystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class responsible for providing a JDBC Connection to the SQLite database.
 * TODO: set the DB file path (e.g. "jdbc:sqlite:orders.db")
 * TODO: implement getConnection()
 * TODO: implement initializeSchema() -> run schema.sql on first startup (AC9)
 */
public class DbConnection {

    private static final String DB_URL = "jdbc:sqlite:orders.db"; // TODO confirm path
    private static final String SCHEMA_RESOURCE = "/schema.sql";
    private static final Path SCHEMA_FILE_FALLBACK = Path.of("src", "main", "resources", "schema.sql");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
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
        private static String readSchema() {
        try (InputStream in = DbConnection.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            // fall through to the file-system lookup below
        }
        try {
            return Files.readString(SCHEMA_FILE_FALLBACK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.sql", e);
        }
    }
    }
    


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
 
public class DbConnection {
 
    private static final String DB_URL = "jdbc:sqlite:orders.db"; // TODO confirm path
    private static final String SCHEMA_RESOURCE = "/schema.sql";
    private static final Path SCHEMA_FILE_FALLBACK = Path.of("src", "main", "resources", "schema.sql");
 
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
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
            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }
 
    private static String readSchema() {
        String rawSchema;
        try (InputStream in = DbConnection.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in != null) {
                rawSchema = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                rawSchema = readSchemaFromFile();
            }
        } catch (IOException e) {
            rawSchema = readSchemaFromFile();
        }
        return stripSqlComments(rawSchema);
    }
 
    private static String readSchemaFromFile() {
        try {
            return Files.readString(SCHEMA_FILE_FALLBACK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.sql", e);
        }
    }
 
    private static String stripSqlComments(String sql) {
        StringBuilder contents = new StringBuilder();
        for (String line : sql.split("\\R")) {
            String cleaned = line.replaceAll("--.*$", "").trim();
            if (!cleaned.isEmpty()) {
                contents.append(cleaned).append(' ');
            }
        }
        return contents.toString();
    }
}
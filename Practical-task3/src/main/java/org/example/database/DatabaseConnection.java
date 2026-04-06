package org.example.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DatabaseConnection {

    static Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("POSTGRES_URL");
    private static final String USER = dotenv.get("POSTGRES_USER");
    private static final String PASSWORD = dotenv.get("POSTGRES_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connected to PostgreSQL successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to PostgreSQL");
            e.printStackTrace();
        }
    }
}
package org.example;

import java.sql.*;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

public class App {

    // JDBC URL, username, password
    static Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASSWORD");

    public static void main(String[] args) {
        try {
            //  Load driver explicitly (optional but safe)
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL Driver loaded successfully!");

            //  Connect to the database
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                System.out.println("Connected to PostgreSQL successfully!");

                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter card number to search: ");
                String cardInput = scanner.nextLine();

                // 🔹 Unsafe example: Statement (SQL injection possible)
                System.out.println("\n[Using Statement] - SQL Injection Vulnerable");
                String sqlUnsafe = "SELECT card_id, card_number FROM cards WHERE card_number = '" + cardInput + "'";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlUnsafe)) {

                    while (rs.next()) {
                        System.out.println("Card ID: " + rs.getInt("card_id") +
                                ", Card Number: " + rs.getString("card_number"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //  Safe example: PreparedStatement
                System.out.println("\n[Using PreparedStatement] - Safe from SQL Injection");
                String sqlSafe = "SELECT card_id, card_number FROM cards WHERE card_number = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlSafe)) {
                    ps.setString(1, cardInput);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        System.out.println("Card ID: " + rs.getInt("card_id") +
                                ", Card Number: " + rs.getString("card_number"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.err.println("Connection failed!");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver not found. Add the driver to your classpath!");
            e.printStackTrace();
        }
    }
}
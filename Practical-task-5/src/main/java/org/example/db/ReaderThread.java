package org.example.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReaderThread {

    public static void main(String[] args) throws Exception {

        Connection conn = DbConfig.getConnection();

        // Change this line for experiments
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

//         conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        conn.setAutoCommit(false);

        Statement st = conn.createStatement();

        // First read
        ResultSet rs1 = st.executeQuery(
                "SELECT amount FROM transactions WHERE transaction_id='TXN_TEST'"
        );

        while (rs1.next()) {
            System.out.println("First Read: " + rs1.getDouble("amount"));
        }

        // Wait while writer commits
        Thread.sleep(8000);

        // Second read
        ResultSet rs2 = st.executeQuery(
                "SELECT amount FROM transactions WHERE transaction_id='TXN_TEST'"
        );

        while (rs2.next()) {
            System.out.println("Second Read: " + rs2.getDouble("amount"));
        }

        conn.commit();
        conn.close();
    }
}
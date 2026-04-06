package org.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class WriterThread {

    public static void main(String[] args) throws Exception {

        Connection conn = DbConfig.getConnection();
        conn.setAutoCommit(false);

        PreparedStatement ps = conn.prepareStatement(
                "UPDATE transactions SET amount = 9999 WHERE transaction_id = ?"
        );

        ps.setString(1, "TXN_TEST");
        ps.executeUpdate();

        System.out.println("Updated but not committed...");
        Thread.sleep(10000);

        conn.commit();
        conn.rollback();
        System.out.println("Committed");

        conn.close();
    }
}
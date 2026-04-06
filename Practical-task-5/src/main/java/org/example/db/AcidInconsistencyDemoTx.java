package org.example.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AcidInconsistencyDemoTx {

    public static void main(String[] args) throws Exception {

        Connection conn = DbConfig.getConnection();

        try {
            conn.setAutoCommit(false);

            // Step 1: Insert merchant
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO merchants(merchant_name, merchant_category) VALUES (?, ?) ON CONFLICT DO NOTHING"
            );
            ps1.setString(1, "Temp Merchant");
            ps1.setString(2, "Test");
            ps1.executeUpdate();

            // Step 2: Insert transaction (FAIL)
            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO transactions(transaction_id, card_id, amount, transaction_time, status, merchant_id, location_id) " +
                            "VALUES (?, ?, ?, NOW(), ?, ?, ?)"
            );

            ps2.setString(1, "TXN_WITH_TX");
            ps2.setInt(2, 9999); // invalid FK
            ps2.setDouble(3, 200);
            ps2.setString(4, "SUCCESS");
            ps2.setInt(5, 1);
            ps2.setInt(6, 1);

            ps2.executeUpdate();

            conn.commit();

        } catch (Exception e) {
            conn.rollback();
            System.out.println("Transaction rolled back successfully");
        }

        conn.close();
    }
}
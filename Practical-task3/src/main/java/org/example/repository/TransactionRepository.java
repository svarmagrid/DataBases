package org.example.repository;

import org.example.database.DatabaseConnection;
import org.example.model.*;

import java.sql.*;
import java.util.*;

public class TransactionRepository {

//     Save a transaction along with state and location(s)
    public void save(Transaction t, String state) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            //Get or Insert Card
            int cardId;
            String getCardSql = "SELECT card_id FROM cards WHERE card_number=?";
            try (PreparedStatement ps = conn.prepareStatement(getCardSql)) {
                ps.setString(1, t.getCard().getCardNumber());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    cardId = rs.getInt("card_id");
                } else {
                    String insertCard = "INSERT INTO cards(card_number) VALUES (?) RETURNING card_id";
                    try (PreparedStatement psInsert = conn.prepareStatement(insertCard)) {
                        psInsert.setString(1, t.getCard().getCardNumber());
                        ResultSet rs2 = psInsert.executeQuery();
                        rs2.next();
                        cardId = rs2.getInt("card_id");
                    }
                }
            }

            //  Insert into transactions
            String txnSql = "INSERT INTO transactions(transaction_id, card_id, amount, timestamp) VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(txnSql)) {
                ps.setString(1, t.getTransactionId());
                ps.setInt(2, cardId);
                ps.setDouble(3, t.getAmount());
                ps.setTimestamp(4, Timestamp.valueOf(t.getTimestamp()));
                ps.executeUpdate();
            }


            //Insert state into transaction_details
            String detailSql = "INSERT INTO transaction_details(transaction_id, state) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(detailSql)) {
                ps.setString(1, t.getTransactionId());
                ps.setString(2, state);
                ps.executeUpdate();
            }


            // Insert locations & mapping
            if (t.getLocations() != null) {
                for (Location loc : t.getLocations()) {
                    int locId;

                    // Check if location exists
                    String getLoc = "SELECT location_id FROM locations WHERE location_name=?";
                    try (PreparedStatement psLoc = conn.prepareStatement(getLoc)) {
                        psLoc.setString(1, loc.getLocationName());
                        ResultSet rsLoc = psLoc.executeQuery();
                        if (rsLoc.next()) {
                            locId = rsLoc.getInt("location_id");
                        } else {
                            String insertLoc = "INSERT INTO locations(location_name) VALUES (?) RETURNING location_id";
                            try (PreparedStatement psInsertLoc = conn.prepareStatement(insertLoc)) {
                                psInsertLoc.setString(1, loc.getLocationName());
                                ResultSet rs2 = psInsertLoc.executeQuery();
                                rs2.next();
                                locId = rs2.getInt("location_id");
                            }
                        }
                    }

                    // Map transaction to location
                    String mapLoc = "INSERT INTO transaction_locations(transaction_id, location_id) VALUES (?,?)";
                    try (PreparedStatement psMap = conn.prepareStatement(mapLoc)) {
                        psMap.setString(1, t.getTransactionId());
                        psMap.setInt(2, locId);
                        psMap.executeUpdate();
                    }
                }
            }


            // Insert merchants if needed
            if (t.getMerchants() != null) {
                for (Merchant m : t.getMerchants()) {
                    int mId;

                    // Check if merchant exists
                    String getMerch = "SELECT merchant_id FROM merchants WHERE merchant_name=?";
                    try (PreparedStatement psM = conn.prepareStatement(getMerch)) {
                        psM.setString(1, m.getMerchantName());
                        ResultSet rsM = psM.executeQuery();
                        if (rsM.next()) {
                            mId = rsM.getInt("merchant_id");
                        } else {
                            String insertM = "INSERT INTO merchants(merchant_name) VALUES (?) RETURNING merchant_id";
                            try (PreparedStatement psInsertM = conn.prepareStatement(insertM)) {
                                psInsertM.setString(1, m.getMerchantName());
                                ResultSet rs2 = psInsertM.executeQuery();
                                rs2.next();
                                mId = rs2.getInt("merchant_id");
                            }
                        }
                    }

                    // Map transaction to merchant
                    String mapM = "INSERT INTO transaction_merchants(transaction_id, merchant_id) VALUES (?,?)";
                    try (PreparedStatement psMap = conn.prepareStatement(mapM)) {
                        psMap.setString(1, t.getTransactionId());
                        psMap.setInt(2, mId);
                        psMap.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Fetch transactions optionally filtered by card number */
    public List<Transaction> getTransactions(String cardNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT t.transaction_id, t.amount, t.timestamp, td.state,
                   c.card_id, c.card_number,
                   l.location_id, l.location_name,
                   m.merchant_id, m.merchant_name
            FROM transactions t
            JOIN cards c ON t.card_id = c.card_id
            LEFT JOIN transaction_details td ON t.transaction_id = td.transaction_id
            LEFT JOIN transaction_locations tl ON t.transaction_id = tl.transaction_id
            LEFT JOIN locations l ON tl.location_id = l.location_id
            LEFT JOIN transaction_merchants tm ON t.transaction_id = tm.transaction_id
            LEFT JOIN merchants m ON tm.merchant_id = m.merchant_id
        """;

        if (cardNumber != null && !cardNumber.isEmpty()) {
            sql += " WHERE c.card_number = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (cardNumber != null && !cardNumber.isEmpty()) {
                ps.setString(1, cardNumber);
            }

            ResultSet rs = ps.executeQuery();
            Map<String, Transaction> map = new HashMap<>();

            while (rs.next()) {
                String txnId = rs.getString("transaction_id");
                Transaction txn = map.get(txnId);
                if (txn == null) {
                    Card card = new Card(rs.getInt("card_id"), rs.getString("card_number"));
                    txn = new Transaction(txnId, card, rs.getDouble("amount"), rs.getTimestamp("timestamp").toLocalDateTime());
                    txn.setDetails(new TransactionDetails(txnId, rs.getString("state")));
                    txn.setLocations(new ArrayList<>());
                    txn.setMerchants(new ArrayList<>());
                    map.put(txnId, txn);
                }

                // Add location
                int locId = rs.getInt("location_id");
                String locName = rs.getString("location_name");
                if (locName != null && txn.getLocations().stream().noneMatch(l -> l.getLocationId() == locId)) {
                    txn.getLocations().add(new Location(locId, locName));
                }

                // Add merchant
                int mId = rs.getInt("merchant_id");
                String mName = rs.getString("merchant_name");
                if (mName != null && txn.getMerchants().stream().noneMatch(m -> m.getMerchantId() == mId)) {
                    txn.getMerchants().add(new Merchant(mId, mName));
                }
            }

            list.addAll(map.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /** Fetch flagged transactions only */
    public List<Transaction> getFlaggedTransactions() {
        List<Transaction> all = getTransactions(null);
        List<Transaction> flagged = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getDetails() != null && "FLAGGED".equalsIgnoreCase(t.getDetails().getState())) {
                flagged.add(t);
            }
        }
        return flagged;
    }

    public List<Transaction> getRecentClearedTransactions(String cardNumber, int count) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
        SELECT t.transaction_id, t.amount, t.timestamp,
               c.card_id, c.card_number,
               td.state
        FROM transactions t
        JOIN cards c ON t.card_id = c.card_id
        LEFT JOIN transaction_details td ON t.transaction_id = td.transaction_id
        WHERE c.card_number = ? AND td.state = 'CLEARED'
        ORDER BY t.timestamp DESC
        LIMIT ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cardNumber);
            ps.setInt(2, count);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Card card = new Card(rs.getInt("card_id"), rs.getString("card_number"));
                Transaction t = new Transaction(
                        rs.getString("transaction_id"),
                        card,
                        rs.getDouble("amount"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
                t.setDetails(new TransactionDetails(rs.getString("transaction_id"), rs.getString("state")));

                // Load the actual locations
                t.setLocations(getTransactionLocations(t.getTransactionId()));

                t.setMerchants(new ArrayList<>()); // load merchants similarly if needed
                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.amount, t.timestamp, c.card_id, c.card_number, d.state " +
                "FROM transactions t " +
                "JOIN cards c ON t.card_id = c.card_id " +
                "LEFT JOIN transaction_details d ON t.transaction_id = d.transaction_id " +
                "ORDER BY t.timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Card card = new Card(rs.getInt("card_id"), rs.getString("card_number"));
                Transaction txn = new Transaction(
                        rs.getString("transaction_id"),
                        card,
                        rs.getDouble("amount"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );

                // Correctly create TransactionDetails with transaction ID
                TransactionDetails details = new TransactionDetails(
                        rs.getString("transaction_id"),   // pass transaction ID
                        rs.getString("state")             // pass state
                );

                txn.setDetails(details);
                list.add(txn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    private List<Location> getTransactionLocations(String transactionId) {
        List<Location> locations = new ArrayList<>();
        String sql = """
        SELECT l.location_id, l.location_name
        FROM transaction_locations tl
        JOIN locations l ON tl.location_id = l.location_id
        WHERE tl.transaction_id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                locations.add(new Location(rs.getInt("location_id"), rs.getString("location_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }
}
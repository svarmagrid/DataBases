package org.example.repository;

import org.example.jdbc.JdbcUtil;
import org.example.model.Card;

import java.util.*;

public class CardRepository {

    private final JdbcUtil jdbc;

    public CardRepository(JdbcUtil jdbc) {
        this.jdbc = jdbc;
    }

    // Insert card
    public void save(String cardNumber) {
        jdbc.execute(
                "INSERT INTO cards(card_number) VALUES (?)",
                cardNumber
        );
    }

    // Find by card number
    public Optional<Card> findByNumber(String cardNumber) {
        return jdbc.findOne(
                "SELECT card_id, card_number FROM cards WHERE card_number = ?",
                rs -> {
                    try {
                        return new Card(
                                rs.getInt("card_id"),
                                rs.getString("card_number")
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                cardNumber
        );
    }

    // Find all cards
    public List<Card> findAll() {
        return jdbc.findMany(
                "SELECT card_id, card_number FROM cards",
                rs -> {
                    try {
                        return new Card(
                                rs.getInt("card_id"),
                                rs.getString("card_number")
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
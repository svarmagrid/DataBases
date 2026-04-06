package org.example.repository;

import org.example.jdbc.JdbcUtil;

import java.util.*;

public class CardRepository {

    private final JdbcUtil jdbc;

    public CardRepository(JdbcUtil jdbc) {
        this.jdbc = jdbc;
    }

    public void save(String cardNumber) {
        jdbc.execute(
                "INSERT INTO cards(card_number) VALUES (?)",
                cardNumber
        );
    }

    public Optional<String> findByNumber(String cardNumber) {
        return jdbc.findOne(
                "SELECT card_number FROM cards WHERE card_number=?",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                cardNumber
        );
    }

    public List<String> findAll() {
        return jdbc.findMany(
                "SELECT card_number FROM cards",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
package org.example;

import org.example.jdbc.JdbcUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcUtilTest {

    JdbcUtil jdbc;

    @BeforeAll
    void setup() {
        jdbc = new JdbcUtil(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );


        // Init schema
        jdbc.execute("""
            CREATE TABLE cards (
                card_id INT AUTO_INCREMENT PRIMARY KEY,
                card_number VARCHAR(50)
            )
        """);

        // Preload data
        jdbc.execute("INSERT INTO cards(card_number) VALUES (?)", "1234");
        jdbc.execute("INSERT INTO cards(card_number) VALUES (?)", "5678");
    }

    // ------------------------
    // POSITIVE TESTS
    // ------------------------

    @BeforeEach
    void resetData() {
        jdbc.execute("DELETE FROM cards");

        jdbc.execute("INSERT INTO cards(card_number) VALUES (?)", "1234");
        jdbc.execute("INSERT INTO cards(card_number) VALUES (?)", "5678");
    }

    @Test
    void testFindOne_success() {
        Optional<String> card = jdbc.findOne(
                "SELECT card_number FROM cards WHERE card_number=?",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "1234"
        );

        Assertions.assertTrue(card.isPresent());
        Assertions.assertEquals("1234", card.get());
    }

    @Test
    void testFindMany_success() {
        List<String> cards = jdbc.findMany(
                "SELECT card_number FROM cards",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        Assertions.assertEquals(2, cards.size());
        Assertions.assertTrue(cards.contains("1234"));
        Assertions.assertTrue(cards.contains("5678"));
    }

    @Test
    void testInsert_newCard() {
        jdbc.execute("INSERT INTO cards(card_number) VALUES (?)", "9999");

        List<String> cards = jdbc.findMany(
                "SELECT card_number FROM cards WHERE card_number=?",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "9999"
        );

        Assertions.assertEquals(1, cards.size());
        Assertions.assertEquals("9999", cards.get(0));
    }

    // ------------------------
    // NEGATIVE TESTS
    // ------------------------

    @Test
    void testFindOne_notFound() {
        Optional<String> card = jdbc.findOne(
                "SELECT card_number FROM cards WHERE card_number=?",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "0000"
        );

        Assertions.assertTrue(card.isEmpty());
    }

    @Test
    void testFindMany_emptyResult() {
        List<String> cards = jdbc.findMany(
                "SELECT card_number FROM cards WHERE card_number=?",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "0000"
        );

        Assertions.assertTrue(cards.isEmpty());
    }

    @Test
    void testInvalidQuery_shouldThrowException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            jdbc.findMany(
                    "SELECT invalid_column FROM cards",
                    rs -> {
                        try {
                            return rs.getString("invalid_column");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        });
    }

    // ------------------------
    // EDGE CASES
    // ------------------------

    @Test
    void testNullParameter() {
        List<String> cards = jdbc.findMany(
                "SELECT card_number FROM cards WHERE card_number IS NULL",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        Assertions.assertTrue(cards.isEmpty());
    }

    @Test
    void testFindOne_multipleResults_shouldThrow() {
        jdbc.execute("INSERT INTO cards(card_number) VALUES (?)", "1234");

        Assertions.assertThrows(RuntimeException.class, () -> {
            jdbc.findOne(
                    "SELECT card_number FROM cards WHERE card_number=?",
                    rs -> {
                        try {
                            return rs.getString("card_number");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    "1234"
            );
        });
    }

    @Test
    void testDeleteAndVerify() {
        jdbc.execute("DELETE FROM cards WHERE card_number=?", "5678");

        List<String> cards = jdbc.findMany(
                "SELECT card_number FROM cards",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        Assertions.assertFalse(cards.contains("5678"));
    }

    @Test
    void testUpdateAndVerify() {
        jdbc.execute("UPDATE cards SET card_number=? WHERE card_number=?", "1111", "1234");

        Optional<String> card = jdbc.findOne(
                "SELECT card_number FROM cards WHERE card_number=?",
                rs -> {
                    try {
                        return rs.getString("card_number");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                "1111"
        );

        Assertions.assertTrue(card.isPresent());
        Assertions.assertEquals("1111", card.get());
    }
}
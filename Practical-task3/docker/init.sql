CREATE TABLE cards (
    card_id SERIAL PRIMARY KEY,
    card_number VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    card_id INT REFERENCES cards(card_id),
    amount NUMERIC(10,2) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE transaction_details (
    transaction_id VARCHAR(50) PRIMARY KEY,
    state VARCHAR(20) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
);

CREATE TABLE locations (
    location_id SERIAL PRIMARY KEY,
    location_name VARCHAR(100) NOT NULL
);

CREATE TABLE merchants (
    merchant_id SERIAL PRIMARY KEY,
    merchant_name VARCHAR(100)
);

CREATE TABLE transaction_locations (
    transaction_id VARCHAR(50),
    location_id INT,
    PRIMARY KEY (transaction_id, location_id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (location_id) REFERENCES locations(location_id)
);

CREATE TABLE transaction_merchants (
    transaction_id VARCHAR(50),
    merchant_id INT,
    PRIMARY KEY (transaction_id, merchant_id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id)
);


INSERT INTO cards (card_number)
VALUES ('1234-5678-9012-3456');

INSERT INTO transactions (transaction_id, card_id, amount, timestamp)
VALUES ('TXN001', 1, 250.75, CURRENT_TIMESTAMP);

-- Get one transaction
SELECT * FROM transactions WHERE transaction_id = 'TXN001';


UPDATE transactions
SET amount = 300.00
WHERE transaction_id = 'TXN001';

SELECT * FROM transactions;

DELETE FROM transactions
WHERE transaction_id = 'TXN001';

-- SELECT *
-- FROM transactions
-- WHERE 1=1
--   AND (:minAmount IS NULL OR amount >= :minAmount)
--   AND (:maxAmount IS NULL OR amount <= :maxAmount)
--   AND (:startDate IS NULL OR timestamp >= :startDate)
--   AND (:endDate IS NULL OR timestamp <= :endDate)
-- ORDER BY timestamp DESC, id DESC
--     LIMIT :pageSize OFFSET :offset;

INSERT INTO transactions (transaction_id, card_id, amount, timestamp)
VALUES ('TXN002', 1, 250.75, CURRENT_TIMESTAMP);

SELECT
    c.card_number,
    COUNT(t.transaction_id) AS total_transactions
FROM cards c
         LEFT JOIN transactions t ON c.card_id = t.card_id
GROUP BY c.card_number;

INSERT INTO cards (card_number) VALUES
                                    ('1111-2222-3333-4444'),
                                    ('5555-6666-7777-8888');

INSERT INTO transactions (transaction_id, card_id, amount, timestamp) VALUES
                                                                          ('TXN1', 1, 250.00, '2026-03-21 10:00:00'),
                                                                          ('TXN2', 1, 300.00, '2026-03-21 12:00:00'),
                                                                          ('TXN3', 2, 150.00, '2026-03-21 14:00:00'),
                                                                          ('TXN4', 2, 400.00, '2026-03-22 09:00:00'),
                                                                          ('TXN5', 1, 500.00, '2026-03-22 11:00:00');

INSERT INTO merchants (merchant_name) VALUES
                                          ('Amazon'),
                                          ('Flipkart'),
                                          ('Walmart');

INSERT INTO transaction_merchants (transaction_id, merchant_id) VALUES
                                                                    ('TXN1', 1),
                                                                    ('TXN2', 1),
                                                                    ('TXN3', 2),
                                                                    ('TXN4', 3),
                                                                    ('TXN5', 1);

SELECT m.merchant_name, COUNT(tm.transaction_id) AS transaction_count
FROM merchants m
         JOIN transaction_merchants tm ON m.merchant_id = tm.merchant_id
GROUP BY m.merchant_name
ORDER BY transaction_count DESC
    LIMIT 5;

INSERT INTO transaction_details (transaction_id, state)
SELECT transaction_id, 'CLEARED'
FROM transactions
WHERE transaction_id NOT IN (
    SELECT transaction_id FROM transaction_details
);

select * from transaction_details;
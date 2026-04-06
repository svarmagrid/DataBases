-- ===============================
-- WITHOUT INDEX (Run BEFORE creating index)
-- ===============================
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1;

-- ===============================
-- AFTER INDEX
-- ===============================
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1;

-- ===============================
-- COMPOUND INDEX (FULL MATCH)
-- ===============================
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1 AND status = 'SUCCESS';

-- ===============================
-- COMPOUND INDEX (PARTIAL MATCH)
-- ===============================
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1;

-- ===============================
-- COMPOUND INDEX (WRONG ORDER)
-- ===============================
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE status = 'SUCCESS';

-- ===============================
-- FRAUD-LIKE QUERY (REALISTIC)
-- ===============================
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1
ORDER BY transaction_time DESC
    LIMIT 10;
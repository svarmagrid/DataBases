-- Single Column Index
CREATE INDEX idx_transactions_card_id
    ON transactions(card_id);

-- Index on status
CREATE INDEX idx_transactions_status
    ON transactions(status);

-- Compound Index
CREATE INDEX idx_transactions_card_status
    ON transactions(card_id, status);

-- Optional: time-based index (useful for fraud detection)
CREATE INDEX idx_transactions_time
    ON transactions(transaction_time);

-- CREATE INDEX idx_transactions_card_id
--     ON transactions(card_id);
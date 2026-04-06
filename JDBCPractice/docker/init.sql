CREATE TABLE cards (
    card_id SERIAL PRIMARY KEY,
    card_number VARCHAR(50) UNIQUE NOT NULL
);

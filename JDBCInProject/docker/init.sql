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
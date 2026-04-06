INSERT INTO transactions (
    transaction_id,
    card_id,
    amount,
    currency,
    transaction_time,
    status,
    payment_channel,
    merchant_id,
    location_id
)
SELECT
    'TXN-' || gs,
    (SELECT card_id FROM cards ORDER BY random() LIMIT 1),
    (random()*10000)::numeric(12,2),
    'INR',
    NOW() - (random()*100000 || ' seconds')::interval,
    CASE
        WHEN random() > 0.8 THEN 'FLAGGED'
        ELSE 'SUCCESS'
END,
    'ONLINE',
    1,
    1
FROM generate_series(1, 2000000) gs;
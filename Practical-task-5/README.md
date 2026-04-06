# Database Systems Practical Task – ACID, Isolation, and Indexing

##  Overview

This project demonstrates key database concepts using PostgreSQL and JDBC:

* ACID properties (Consistency violation & fix)
* Transaction isolation levels
* Indexing and performance optimization
* Compound index behavior

---

# 1. ACID Property – Consistency Violation

## Objective

Demonstrate how lack of transactions can lead to an inconsistent database state.

---

## Scenario (Without Transaction)

Steps:

1. Insert a merchant (valid operation)
2. Insert a transaction with invalid foreign key (fails)

### Java Code

```java
conn.setAutoCommit(true);

// Step 1: Insert merchant
INSERT INTO merchants(merchant_name, merchant_category)

// Step 2: Insert transaction (invalid FK → FAIL)
INSERT INTO transactions(card_id = 9999)
```

---

## Output

* Merchant inserted successfully
* Transaction failed due to FK constraint

---

## Verification

```sql
SELECT * FROM merchants WHERE merchant_name = 'Temp Merchant';
```

Result:

```
Row exists 
```

```sql
SELECT * FROM transactions WHERE transaction_id = 'TXN_NO_TX';
```

Result:

```
No rows 
```

---

## Conclusion

* Partial data is committed
* Database becomes inconsistent

---

# 2. ACID Property – Using Transaction (Fix)

## Objective

Ensure atomicity and consistency using transactions.

---

## Scenario (With Transaction)

Steps:

1. Begin transaction
2. Insert merchant
3. Insert invalid transaction (fails)
4. Rollback entire transaction

---

## Java Code

```java
conn.setAutoCommit(false);

try {
    // Insert merchant
    INSERT INTO merchants(...);

    // Insert invalid transaction
    INSERT INTO transactions(...);

    conn.commit();
} catch (Exception e) {
    conn.rollback();
}
```

---

## Output

* Error occurs
* Transaction rolled back

---

## Verification

```sql
SELECT * FROM merchants WHERE merchant_name = 'Temp Merchant';
```

Result:

```
No rows 
```

---

## Conclusion

* No partial updates
* Database remains consistent

---

# 3. Transaction Isolation Levels

## Objective

Demonstrate concurrent transaction behavior.

---

## Scenario

Two threads:

### Writer Thread

* Updates transaction amount but does not commit immediately

### Reader Thread

* Reads data while writer transaction is still running

---

## Default Isolation (READ COMMITTED)

Observation:

* Reader sees only committed data
* No dirty reads

---

## Higher Isolation (SERIALIZABLE)

```java
conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
```

Observation:

* Strict consistency maintained
* Prevents anomalies like:

    * Dirty reads
    * Non-repeatable reads
    * Phantom reads

---

## Conclusion

| Isolation Level | Behavior                    |
| --------------- | --------------------------- |
| READ COMMITTED  | No dirty reads              |
| SERIALIZABLE    | Fully consistent but slower |

---

# 4. Indexing and Performance

## Objective

Improve query performance using indexes.

---

## Without Index

```sql
EXPLAIN ANALYZE
SELECT * FROM transactions WHERE card_id = 1;
```

### Output

```
Seq Scan
Execution Time ≈ High
```

---

##  With Index

```sql
CREATE INDEX idx_transactions_card_id ON transactions(card_id);
```

```sql
EXPLAIN ANALYZE
SELECT * FROM transactions WHERE card_id = 1;
```

---

##  Observation

* Index may or may not be used
* Depends on data distribution

---

##  Conclusion

* Index improves performance for selective queries
* Not useful when most rows match

---

# 5. Compound Index

##  Objective

Analyze multi-column index behavior.

---

## Index Creation

```sql
CREATE INDEX idx_card_status ON transactions(card_id, status);
```

---

## Query 1 (Full Match)

```sql
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1 AND status = 'SUCCESS';
```

---

## Output

```
Seq Scan (Index not used)
Execution Time ≈ 160 ms
```

---

## Reason

* Query returns large number of rows (~80%)
* Index not efficient

---

## Query 2 (Selective Query)

```sql
EXPLAIN ANALYZE
SELECT * FROM transactions
WHERE card_id = 1 AND status = 'FLAGGED';
```

---

## Output

```
Index Scan / Bitmap Index Scan
Execution Time ↓
```

---

## Partial Column Usage

```sql
SELECT * FROM transactions WHERE card_id = 1;
```

✔ Index used (leftmost column)

---

```sql
SELECT * FROM transactions WHERE status = 'SUCCESS';
```

Index not used

---

##  Conclusion

* Compound index works best when:

    * All columns are used
    * Leftmost column is included
* Index effectiveness depends on selectivity

---

#  Final Observations

| Concept                  | Result                           |
| ------------------------ | -------------------------------- |
| ACID without transaction | Inconsistent                     |
| ACID with transaction    | Consistent                       |
| Isolation levels         | Control concurrency              |
| Indexing                 | Improves performance selectively |
| Compound index           | Depends on query pattern         |

---

# Final Conclusion

This project demonstrates:

* Importance of transactions for data integrity
* Role of isolation levels in concurrency control
* Performance impact of indexing
* Behavior of compound indexes in real-world queries

---


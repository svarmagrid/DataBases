# Learning Summary – Practical Task

##  New Concepts Learned

### 1. Database Design
- Understood how to design relational databases using multiple tables.
- Learned how to normalize data into separate entities like:
    - Cards
    - Transactions
    - Merchants
    - Locations
- Implemented **foreign key relationships** to maintain data integrity.

---

### 2. SQL CRUD Operations
- Practiced basic SQL operations:
    - **CREATE** – inserting new records
    - **READ** – fetching data using SELECT
    - **UPDATE** – modifying existing records
    - **DELETE** – removing records
- Learned importance of inserting parent records before child records.

---

### 3. Joins and Relationships
- Learned how to use:
    - `INNER JOIN`
    - `LEFT JOIN`
- Combined data from multiple tables (e.g., transactions with merchants and locations).
- Understood how junction tables (many-to-many relationships) work.

---

### 4. Dynamic Queries
- Built flexible queries using conditions like:
  ```sql
  (:param IS NULL OR column = :param)
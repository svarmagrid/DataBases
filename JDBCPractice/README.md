# Statement vs PreparedStatement in Java (JDBC)

## 1. Overview

In Java JDBC, both `Statement` and `PreparedStatement` are used to execute SQL queries, but they differ significantly in **security, performance, and usability**.

---

## 2. Key Differences

| Feature       | Statement                | PreparedStatement             |
| ------------- | ------------------------ | ----------------------------- |
| Query Type    | Static SQL               | Precompiled SQL with parameters |
| Performance   | Slower (parsed every time) | Faster (compiled once, reused) |
| SQL Injection | Vulnerable               | Safe                          |
| Parameters    | Manual concatenation     | Uses `?` placeholders         |
| Readability   | Harder to maintain       | Cleaner and safer             |

---

## 3. SQL Injection Explained

SQL Injection is a security vulnerability where an attacker can manipulate SQL queries by injecting malicious input.

Example input:

```
' OR '1'='1
```

This can make a query always return true.

---

## 4. Example Using Statement (Vulnerable)

```java
import java.sql.*;

public class StatementExample {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "password"
        );

        Statement stmt = conn.createStatement();

        String userInput = "' OR '1'='1"; // malicious input

        String query = "SELECT * FROM cards WHERE card_number = '" + userInput + "'";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("Card: " + rs.getString("card_number"));
        }

        conn.close();
    }
}
```

### ❌ Problem:

* The query becomes:

```
SELECT * FROM cards WHERE card_number = '' OR '1'='1'
```

* This returns **all records** → security breach.

---

## 5. Example Using PreparedStatement (Safe)

```java
import java.sql.*;

public class PreparedStatementExample {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "password"
        );

        String query = "SELECT * FROM cards WHERE card_number = ?";
        PreparedStatement ps = conn.prepareStatement(query);

        String userInput = "' OR '1'='1"; // same malicious input
        ps.setString(1, userInput);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println("Card: " + rs.getString("card_number"));
        }

        conn.close();
    }
}
```

### ✅ Why it's safe:

* Input is treated as **data**, not SQL.
* Query structure is unchanged.
* No injection possible.

---

## 6. Docker Database Setup

Make sure PostgreSQL is running in Docker:

```bash
docker run --name postgres-db \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres
```

---

## 7. DriverManager Connection

```java
Connection conn = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/postgres",
    "postgres",
    "password"
);
```

---

## 8. Conclusion

* Use `Statement` only for simple, static queries.
* Always prefer `PreparedStatement` for:

    * User input
    * Dynamic queries
    * Security-critical applications

👉 **Best Practice:** Always use `PreparedStatement` to prevent SQL injection.

---

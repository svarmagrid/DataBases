# Connection Pooling in Java

## What is it?

Connection pooling is a technique where a pool of database connections is maintained and reused.

---

## Why needed?

Creating DB connections is expensive:
- Network overhead
- Authentication cost
- Resource allocation

---

## Without Pooling

- Each request creates a new connection
- Slow performance
- High CPU usage

---

## With Pooling

- Connections are reused
- Faster response time
- Better scalability

---

## Advantages

- High performance
- Efficient resource usage
- Supports concurrency

---

## Disadvantages

- Slight memory overhead
- Requires configuration
- Risk of connection leaks

---

## Experiment Result

### Single Connection
- Threads: 5
- Time: ~10 seconds

### HikariCP Pool
- Threads: 5
- Time: ~2 seconds

---

## Conclusion

Connection pooling is essential for production applications.
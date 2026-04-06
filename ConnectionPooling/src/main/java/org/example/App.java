package org.example;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.datasource.HikariDataSourceFactory;
import org.example.datasource.SingleConnectionDataSource;
import org.example.jdbc.JdbcUtil;
import org.example.repository.CardRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws Exception {

        // -----------------------------
        // DB CONFIG
        // -----------------------------
        Dotenv dotenv=Dotenv.load();
        String url= dotenv.get("POSTGRES_URL");
        String user=dotenv.get("POSTGRES_USER");
        String password= dotenv.get("POSTGRES_PASSWORD");

        // -----------------------------
        // SWITCH DATA SOURCE HERE
        // -----------------------------

        //  Option 1: Single connection (slow)
//         DataSource ds = new SingleConnectionDataSource(url, user, password);

        // ⚡ Option 2: HikariCP (fast - pooled)
        DataSource ds = HikariDataSourceFactory.create(url, user, password);

        JdbcUtil jdbc = new JdbcUtil(ds);
        CardRepository repo = new CardRepository(jdbc);

        // -----------------------------
        // CREATE TABLE
        // -----------------------------
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS cards (
                card_id SERIAL PRIMARY KEY,
                card_number VARCHAR(50)
            )
        """);

        // -----------------------------
        // INSERT SAMPLE DATA
        // -----------------------------
        repo.save("1111-2222");
        repo.save("3333-4444");

        // -----------------------------
        // FETCH DATA
        // -----------------------------
        System.out.println("All Cards:");
        repo.findAll().forEach(System.out::println);

        // -----------------------------
        // MULTI-THREAD TEST
        // -----------------------------
        int threads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try (Connection conn = ds.getConnection();
                     Statement stmt = conn.createStatement()) {

                    String threadName = Thread.currentThread().getName();
                    System.out.println(threadName + " running...");

                    // Simulate long DB task
                    stmt.execute("SELECT pg_sleep(2)");

                    System.out.println(threadName + " finished");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown executor
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();

        System.out.println("Total Time: " + (end - start) + " ms");

        // -----------------------------
        // VERY IMPORTANT: CLOSE POOL
        // -----------------------------
        if (ds instanceof HikariDataSource hikari) {
            hikari.close();
        }

        System.out.println("Application finished cleanly.");
    }
}
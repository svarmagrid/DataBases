package org.example.multithreading;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.datasource.HikariDataSourceFactory;
import org.example.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadTest {

    public static void main(String[] args) throws Exception {

        Dotenv dotenv=Dotenv.load();
        String url= dotenv.get("POSTGRES_URL");
        String user=dotenv.get("POSTGRES_USER");
        String password= dotenv.get("POSTGRES_PASSWORD");

        // Create both datasources
        DataSource singleDS = new SingleConnectionDataSource(url, user, password);
        DataSource hikariDS = HikariDataSourceFactory.create(url, user, password);

        System.out.println("===== Testing SingleConnectionDataSource =====");
        runMultiThreadTest(singleDS);

        System.out.println("\n===== Testing HikariCP Connection Pool =====");
        runMultiThreadTest(hikariDS);
    }

    private static void runMultiThreadTest(DataSource ds) throws InterruptedException {
        int threads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try (Connection conn = ds.getConnection();
                     Statement stmt = conn.createStatement()) {

                    System.out.println(Thread.currentThread().getName() + " running...");

                    // Simulate long-running DB operation
                    stmt.execute("SELECT pg_sleep(2)");

                    System.out.println(Thread.currentThread().getName() + " finished");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("Total Time: " + (end - start) + " ms");
    }
}
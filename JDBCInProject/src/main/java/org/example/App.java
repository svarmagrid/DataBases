package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.database.DatabaseConnection;
import org.example.jdbc.JdbcUtil;
import org.example.model.*;
import org.example.observer.Observer;
import org.example.repository.CardRepository;
import org.example.repository.TransactionRepository;
import org.example.service.*;
import org.example.observer.*;
import org.example.state.*;

import java.util.*;
import java.time.LocalDateTime;

public class App {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        DatabaseConnection.testConnection();
        Scanner scanner = new Scanner(System.in);

        // -----------------------------------------
        // JDBC + Repositories
        // -----------------------------------------
        JdbcUtil jdbc = new JdbcUtil(
                dotenv.get("DB_URL"),
                dotenv.get("DB_USER"),
                dotenv.get("DB_PASSWORD")
        );

        CardRepository cardRepo = new CardRepository(jdbc);
        TransactionRepository repo = new TransactionRepository();

        FraudDetectionService fraudService = new FraudDetectionService();

        // -----------------------------------------
        // Observers
        // -----------------------------------------
        Observer logger = new LoggingService();
        Observer alert = new FraudAlertService();

        System.out.print("Enter your email for notifications: ");
        String userEmail = scanner.nextLine();
        Observer emailNotifier = new EmailNotificationService(userEmail);

        // -----------------------------------------
        // Main Loop
        // -----------------------------------------
        while (true) {
            System.out.println("\n===== FRAUD MONITOR MENU =====");
            System.out.println("1. Add Transaction");
            System.out.println("2. View All Transactions");
            System.out.println("3. View Flagged Transactions");
            System.out.println("4. View All Cards");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                // -----------------------------------------
                // Add Transaction
                // -----------------------------------------
                case "1":
                    try {
                        System.out.print("Enter Card Number: ");
                        String cardNumber = scanner.nextLine();

                        System.out.print("Enter Amount: ");
                        double amount = Double.parseDouble(scanner.nextLine());

                        // -----------------------------------------
                        // Use CardRepository (IMPORTANT PART)
                        // -----------------------------------------
                        Optional<String> existingCard = cardRepo.findByNumber(cardNumber);

                        if (existingCard.isEmpty()) {
                            cardRepo.save(cardNumber);
                            System.out.println("Card saved to DB.");
                        } else {
                            System.out.println("Card already exists.");
                        }

                        // -----------------------------------------
                        // Location
                        // -----------------------------------------
                        String locationName = LocationService.getLocation();
                        System.out.println("Detected Location: " + locationName);

                        // -----------------------------------------
                        // Create Objects
                        // -----------------------------------------
                        Card cardObj = new Card(0, cardNumber);
                        Location loc = new Location(0, locationName);

                        String txnId = "TXN-" + UUID.randomUUID();

                        Transaction txn = new Transaction(
                                txnId,
                                cardObj,
                                amount,
                                LocalDateTime.now()
                        );

                        txn.setLocations(List.of(loc));

                        // -----------------------------------------
                        // Context + Observers
                        // -----------------------------------------
                        TransactionContext context = new TransactionContext(txn);
                        context.addObserver(logger);
                        context.addObserver(alert);
                        context.addObserver(emailNotifier);

                        // -----------------------------------------
                        // State + Fraud Evaluation
                        // -----------------------------------------
                        context.setState(new PendingState());
                        fraudService.evaluate(context);

                        System.out.println("Transaction processed successfully!");

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                // -----------------------------------------
                // View All Transactions
                // -----------------------------------------
                case "2":
                    List<Transaction> allTxns = repo.getAllTransactions();

                    System.out.println("\n--- All Transactions ---");

                    if (allTxns.isEmpty()) {
                        System.out.println("No transactions found.");
                        break;
                    }

                    for (Transaction t : allTxns) {
                        System.out.println(
                                "TXN ID: " + t.getTransactionId() +
                                        ", Card: " + t.getCard().getCardNumber() +
                                        ", Amount: " + t.getAmount() +
                                        ", Timestamp: " + t.getTimestamp() +
                                        ", State: " +
                                        (t.getDetails() != null ? t.getDetails().getState() : "N/A")
                        );
                    }
                    break;

                // -----------------------------------------
                // View Flagged Transactions
                // -----------------------------------------
                case "3":
                    List<Transaction> flaggedTxns = repo.getFlaggedTransactions();

                    System.out.println("\n--- Flagged Transactions ---");

                    if (flaggedTxns.isEmpty()) {
                        System.out.println("No flagged transactions.");
                        break;
                    }

                    for (Transaction t : flaggedTxns) {
                        System.out.println(
                                "TXN ID: " + t.getTransactionId() +
                                        ", Card: " + t.getCard().getCardNumber() +
                                        ", Amount: " + t.getAmount() +
                                        ", Timestamp: " + t.getTimestamp()
                        );
                    }
                    break;

                // -----------------------------------------
                // View All Cards (USES CardRepository)
                // -----------------------------------------
                case "4":
                    List<String> cards = cardRepo.findAll();

                    System.out.println("\n--- All Cards ---");

                    if (cards.isEmpty()) {
                        System.out.println("No cards found.");
                        break;
                    }

                    for (String c : cards) {
                        System.out.println("Card: " + c);
                    }
                    break;

                // -----------------------------------------
                // Exit
                // -----------------------------------------
                case "5":
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
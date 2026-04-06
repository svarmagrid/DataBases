package org.example;

import org.example.database.DatabaseConnection;
import org.example.model.*;
import org.example.observer.Observer;
import org.example.repository.TransactionRepository;
import org.example.service.*;
import org.example.observer.*;
import org.example.state.*;

import java.util.*;
import java.time.LocalDateTime;

public class App {

    public static void main(String[] args) {
        DatabaseConnection.testConnection();
        Scanner scanner = new Scanner(System.in);

        TransactionRepository repo = new TransactionRepository();
        FraudDetectionService fraudService = new FraudDetectionService();

        // Observers
        Observer logger = new LoggingService();
        Observer alert = new FraudAlertService();

        System.out.print("Enter your email for notifications: ");
        String userEmail = scanner.nextLine();
        Observer emailNotifier = new EmailNotificationService(userEmail);

        while (true) {
            System.out.println("\n===== FRAUD MONITOR MENU =====");
            System.out.println("1. Add Transaction");
            System.out.println("2. View All Transactions");
            System.out.println("3. View Flagged Transactions");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Card Number: ");
                    String cardNumber = scanner.nextLine();
                    System.out.print("Enter Amount: ");
                    double amount = Double.parseDouble(scanner.nextLine());

                    // Get location (single for now)
                    String locationName = LocationService.getLocation();
                    System.out.println("Detected Location: " + locationName);

                    // Create Card and Location objects
                    Card cardObj = new Card(0, cardNumber); // dummy ID for new card
                    Location loc = new Location(0, locationName); // dummy ID for new location

                    // Create Transaction object
                    String txnId = "TXN-" + UUID.randomUUID();
                    Transaction txn = new Transaction(txnId, cardObj, amount, LocalDateTime.now());
                    txn.setLocations(List.of(loc));

                    // Transaction context with observers
                    TransactionContext context = new TransactionContext(txn);
                    context.addObserver(logger);
                    context.addObserver(alert);
                    context.addObserver(emailNotifier);

                    // Set initial state and evaluate
                    context.setState(new PendingState());
                    fraudService.evaluate(context);
                    break;

                case "2":
                    // View all transactions as Transaction objects
                    List<Transaction> allTxns = repo.getAllTransactions();
                    System.out.println("\n--- All Transactions ---");
                    for (Transaction t : allTxns) {
                        System.out.println("TXN ID: " + t.getTransactionId() +
                                ", Card: " + t.getCard().getCardNumber() +
                                ", Amount: " + t.getAmount() +
                                ", Timestamp: " + t.getTimestamp() +
                                ", State: " + (t.getDetails() != null ? t.getDetails().getState() : "N/A"));
                    }
                    break;

                case "3":
                    // View flagged transactions
                    List<Transaction> flaggedTxns = repo.getFlaggedTransactions();
                    System.out.println("\n--- Flagged Transactions ---");
                    for (Transaction t : flaggedTxns) {
                        System.out.println("TXN ID: " + t.getTransactionId() +
                                ", Card: " + t.getCard().getCardNumber() +
                                ", Amount: " + t.getAmount() +
                                ", Timestamp: " + t.getTimestamp());
                    }
                    break;

                case "4":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
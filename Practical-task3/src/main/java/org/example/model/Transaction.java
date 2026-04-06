package org.example.model;

import java.time.LocalDateTime;
import java.util.List;

public class Transaction {
    private String transactionId;
    private Card card;
    private double amount;
    private LocalDateTime timestamp;

    private TransactionDetails details;
    private List<Location> locations;
    private List<Merchant> merchants;

    public Transaction(String transactionId, Card card, double amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.card = card;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public Card getCard() { return card; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public TransactionDetails getDetails() { return details; }
    public void setDetails(TransactionDetails details) { this.details = details; }

    public List<Location> getLocations() { return locations; }
    public void setLocations(List<Location> locations) { this.locations = locations; }

    public List<Merchant> getMerchants() { return merchants; }
    public void setMerchants(List<Merchant> merchants) { this.merchants = merchants; }
}
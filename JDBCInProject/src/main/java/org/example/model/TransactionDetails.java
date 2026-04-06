package org.example.model;

public class TransactionDetails {
    private String transactionId;
    private String state;

    public TransactionDetails(String transactionId, String state) {
        this.transactionId = transactionId;
        this.state = state;
    }

    public String getTransactionId() { return transactionId; }
    public String getState() { return state; }
}
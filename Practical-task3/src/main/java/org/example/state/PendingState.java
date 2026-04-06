package org.example.state;

public class PendingState implements TransactionState {
    public String getName() { return "PENDING"; }
    public void handle(TransactionContext context) { System.out.println("Transaction under review..."); }
}
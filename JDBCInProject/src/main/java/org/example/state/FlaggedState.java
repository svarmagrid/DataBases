package org.example.state;

public class FlaggedState implements TransactionState {
    public String getName() { return "FLAGGED"; }
    public void handle(TransactionContext context) { System.out.println("Transaction flagged!"); }
}
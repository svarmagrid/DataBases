package org.example.state;

public class ClearedState implements TransactionState {
    public String getName() { return "CLEARED"; }
    public void handle(TransactionContext context) { System.out.println("Transaction approved."); }
}
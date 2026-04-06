package org.example.service;

import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.example.state.*;

import java.time.LocalDateTime;
import java.util.List;

public class FraudDetectionService {

    private TransactionRepository repo = new TransactionRepository();

    public void evaluate(TransactionContext context) {
        Transaction t = context.getTransaction();
        String card = t.getCard().getCardNumber();

        boolean velocity = checkVelocity(card);
        boolean location = checkLocation(card, t);  // pass the whole Transaction now

        if(velocity || location) {
            context.setState(new FlaggedState());
            repo.save(t, "FLAGGED");
        } else {
            context.setState(new ClearedState());
            repo.save(t, "CLEARED");
        }
    }

//    private boolean checkVelocity(String card) {
//        List<Transaction> lastTxns = repo.getRecentTransactions(card, 3);
//        if(lastTxns.size() < 3) return false;
//        LocalDateTime first = lastTxns.get(2).getTimestamp();
//        LocalDateTime last = lastTxns.get(0).getTimestamp();
//        long seconds = java.time.Duration.between(first, last).getSeconds();
//        return seconds < 60;
//    }
//
//    private boolean checkLocation(String card, String newLocation) {
//        List<Transaction> lastTxns = repo.getRecentTransactions(card, 1);
//        if(lastTxns.isEmpty()) return false;
//        String prev = lastTxns.get(0).getLocation();
//        return !prev.equalsIgnoreCase(newLocation);
//    }

    private boolean checkVelocity(String card) {
        // Only look at the last 3 CLEARED transactions
        List<Transaction> lastTxns = repo.getRecentClearedTransactions(card, 3);
        if(lastTxns.size() < 3) return false;
        LocalDateTime first = lastTxns.get(2).getTimestamp();
        LocalDateTime last = lastTxns.get(0).getTimestamp();
        long seconds = java.time.Duration.between(first, last).getSeconds();
        return seconds < 60;
    }

    private boolean checkLocation(String card, Transaction t) {
        List<Transaction> lastTxns = repo.getRecentClearedTransactions(card, 1);
        if(lastTxns.isEmpty() || t.getLocations().isEmpty()) return false;

        // Get the last cleared transaction's first location
        Transaction prevTxn = lastTxns.get(0);
        String prevLocation = prevTxn.getLocations().isEmpty() ? ""
                : prevTxn.getLocations().get(0).getLocationName();

        // Get new transaction's first location
        String newLocation = t.getLocations().get(0).getLocationName();

        return !prevLocation.equalsIgnoreCase(newLocation);
    }
}
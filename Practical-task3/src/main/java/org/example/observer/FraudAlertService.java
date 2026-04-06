package org.example.observer;

import org.example.model.Transaction;

public class FraudAlertService implements Observer {

    @Override
    public void update(String state, Transaction t) {
        if ("FLAGGED".equals(state)) {
            // Access card number via Card object
            System.out.println("[ALERT] Suspicious transaction for card " + t.getCard().getCardNumber());
        }
    }
}
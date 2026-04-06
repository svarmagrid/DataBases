package org.example.observer;

import org.example.model.Transaction;
import org.example.model.Location;

public class LoggingService implements Observer {

    @Override
    public void update(String state, Transaction t) {
        StringBuilder locations = new StringBuilder();
        if (t.getLocations() != null) {
            for (Location loc : t.getLocations()) {
                if (locations.length() > 0) locations.append(", ");
                locations.append(loc.getLocationName());
            }
        }
        System.out.println("[LOG] Transaction " + t.getTransactionId() +
                " → " + state + " | Location(s): " + locations);
    }
}
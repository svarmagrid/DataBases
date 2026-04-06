package org.example.state;

import org.example.model.Transaction;
import org.example.observer.Observer;
import org.example.observer.Subject;
import java.util.*;

public class TransactionContext implements Subject {
    private TransactionState state;
    private Transaction transaction;
    private List<Observer> observers = new ArrayList<>();

    public TransactionContext(Transaction transaction) {
        this.transaction = transaction;
        this.state = new PendingState();
    }

    public void setState(TransactionState state) {
        this.state = state;
        notifyObservers();
        state.handle(this);
    }

    public Transaction getTransaction() { return transaction; }
    public String getStateName() { return state.getName(); }
    public void addObserver(Observer o) { observers.add(o); }
    public void notifyObservers() {
        for(Observer o : observers) o.update(state.getName(), transaction);
    }
}
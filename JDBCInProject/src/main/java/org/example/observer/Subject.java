package org.example.observer;

public interface Subject {
    void addObserver(Observer o);
    void notifyObservers();
}
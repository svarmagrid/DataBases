package org.example.model;

public class Card {
    private int cardId;
    private String cardNumber;

    public Card(int cardId, String cardNumber) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
    }

    public int getCardId() { return cardId; }
    public String getCardNumber() { return cardNumber; }
}
package com.antonkuzmichev.blackjack_server.game;

import java.util.List;
import java.util.ArrayList;
// PlayerStatusDTO.java
class PlayerStatusDTO {
    private String sessionId;
    private String name;
    private int balance;
    private int bet;
    private int insurance;
    private int handValue;
    private int cardCount;
    private boolean hasActed;
    private boolean isBusted;
    private boolean hasNaturalBlackjack;
    private List<CardDTO> cards;

    public PlayerStatusDTO(String name, int balance, int bet, int insurance,
            int handValue, int cardCount, boolean hasActed,
            boolean isBusted, boolean hasNaturalBlackjack) {
        this.name = name;
        this.balance = balance;
        this.bet = bet;
        this.insurance = insurance;
        this.handValue = handValue;
        this.cardCount = cardCount;
        this.hasActed = hasActed;
        this.isBusted = isBusted;
        this.hasNaturalBlackjack = hasNaturalBlackjack;
        this.cards = new ArrayList<>();
    }

    // Getters and setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getBet() {
        return bet;
    }

    public int getInsurance() {
        return insurance;
    }

    public int getHandValue() {
        return handValue;
    }

    public int getCardCount() {
        return cardCount;
    }

    public boolean hasActed() {
        return hasActed;
    }

    public boolean isBusted() {
        return isBusted;
    }

    public boolean hasNaturalBlackjack() {
        return hasNaturalBlackjack;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardDTO> cards) {
        this.cards = cards;
    }
}



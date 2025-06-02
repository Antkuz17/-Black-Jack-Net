package com.antonkuzmichev.blackjack_server.game;

class CardDTO {
    private String suit;
    private String rank;

    public CardDTO(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }
}
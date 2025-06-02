package com.antonkuzmichev.blackjack_server.game;

// Card (Data Transfer Object), used to represent a playing card in a convient way to send from server to client
class CardDTO {
    private String suit;
    private String rank; // Fields

    // Constructor
    public CardDTO(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    //Getters
    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }
}
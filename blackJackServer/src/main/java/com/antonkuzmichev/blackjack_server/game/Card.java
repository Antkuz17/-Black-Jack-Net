package com.antonkuzmichev.blackjack_server.game;

// This class represents 1 card in a deck (contains methods that will read the card out, getter methods, and can return value of the card)

public class Card {
    private String suit; 
    private String rank;
    //Creating instance variables

    public Card(String suit, String rank){
        this.suit = suit; // Suit of the card
        this.rank = rank; // Rank of card (king, ace, queen, 2, etc)
    }
    /**
     * Reads the card in format (King of Spades)
     * 
     * <li> Prints to terminal
     */
    public void readCard(){
        System.out.println(rank + " of " + suit);
    }
    /**
     * Returns the suit of the card.
     *
     * @return the suit as a String
     */
    public String getSuit(){
        return suit;
    }

    /**
     * Returns the rank of the card.
     *
     * @return the rank as a String
     */
    public String getRank(){
        return(rank);
    }

    /**
     * Returns the integer value associated with the card's rank.
     * <ul>
     *   <li>Number cards ("2"–"10") return their numeric value.</li>
     *   <li>Face cards ("Jack", "Queen", "King") return 10.</li>
     *   <li>"Ace" returns 11 (or 1, depending on game rules).</li>
     *   <li>"cutCard" returns -1, indicating the cut card has been reached and the deck should be reshuffled.</li>
     *   <li>Any unrecognized rank returns 0.</li>
     * </ul>
     *
     * @return the integer value of the card's rank
     */
    public int getValue() {
        switch (rank) {
            case "2": return 2;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 7;
            case "8": return 8;
            case "9": return 9;
            case "10": return 10;
            case "Jack": return 10;
            case "Queen": return 10;
            case "King": return 10;
            case "Ace": return 11; // or 1, depending on game rules(taken into account in the hand method)
            default: return 0; 
        }
    }

}

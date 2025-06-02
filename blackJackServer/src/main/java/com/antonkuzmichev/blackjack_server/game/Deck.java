package com.antonkuzmichev.blackjack_server.game;
//Represents 1 full blackJackDeck. Number of 52 card decks included can be stated when a new deck is created
//Deck itself is an array list, and is created using triple nested for loops
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Iterator;

public class Deck implements Iterable<Card> { // Implements Iterable Interface meaning a for each loop is allowed for cards
    private ArrayList<Card> deck; // Deck is stored as an arrayList
    public int decks;
    public static Random random = new Random();

    public Deck(int decks) { // Constructor
        deck = new ArrayList<Card>(); // Initializing deck as a new ArrayList
        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace" };
        // Nested for loops to create d amount of deck
        for (int d = 0; d < decks; d++) {
            for (String i : suits) {
                for (String x : ranks) {
                    deck.add(new Card(i, x));
                }
            }
        }
    }

    /**
     * Returns an iterator over the cards in the deck.
     * Allows the Deck to be used in for-each loops
     */
    public Iterator<Card> iterator() {
        return deck.iterator();
    }

    /**
     * Shuffles the deck using collections class
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Draws a card from the top of the deck and then removes it
     * 
     * @return The top card of the deck
     */
    public Card draw() { 
        Card drawnCard = deck.get(0); // stores top card as drawnCard
        deck.remove(0); // Removes that card
        return drawnCard; // Returns the drawn card
    }
    
    // Returns the number of cards left in the deck using the .size() method in the ArrayList Library
    public int size(){ 
        return deck.size();
    }



}

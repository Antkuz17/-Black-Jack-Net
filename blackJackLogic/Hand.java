import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Hand class represents a player's hand in a card game.
 * It manages a collection of Card objects, allowing cards to be added,
 * the total value of the hand to be calculated, and checks for bust conditions.
 *
 */

public class Hand implements Iterable<Card>{
    private ArrayList<Card> hand;
    // Creating instance variable of hand

    /**
     * Returns an iterator over the cards in the deck.
     * Allows the Deck to be used in enhanced for-loops.
     */
    public Iterator<Card> iterator() {
        return hand.iterator();
    }

    // Constructor
    public Hand() {
        hand = new ArrayList<Card>();
        // Hand is stored as an array list as you are constantly adding and removing
        // cards from hand
    }

    /**
     * Adds a card to hand
     * 
     * @param card the card to add to the hand
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    public void removeCard(int index){
        hand.remove(index);
    }

    /**
     * Checks whether the total hand value exceeds 21.
     *
     * @return {@code true} if the hand value is greater than 21 (bust),
     *         {@code false} otherwise
     */
    public boolean checkIfBust() {

        return (getTotalValue() > 21);
        // call getTotalValue method to see if bust
    }

    /**
     * Calculates the total value of the hand, treating Aces as either 11 or 1
     * to avoid busting if possible.
     *
     * @return the total value of the hand
     */
    public int getTotalValue() {
        int valueTotal = 0;
        int numAces = 0;

        for (Card i : hand) { // for each card in hand
            valueTotal += i.getValue();
            if (i.getRank().equals("Ace")) {
                numAces++;
            }
        }
        // while the count is over 21 and we still have aces, turn an 11 ace to a 1 ace
        while (valueTotal > 21 && numAces > 0) {
            valueTotal -= 10;
            numAces--;
        }

        return valueTotal;
    }
    /**
     * Checks if a split of your hand is possible by comparing both cards and seeing if they have the same rank
     * @return {@code true} if the hand can be split,
     *         {@code false} otherwise
     */
    public boolean checkIfSplitPossible(){
        if (hand.size() == 2 && hand.get(0).getRank().equals(hand.get(1).getRank())) {
            return true;
        }
        return false;
    }
    /**
     * Prints the hand out to terminal in format:
     * <ul>
     *  <li> Ace of Spades
     *  <li> 4 of clubs
     */
    public void showHand() {
        for (Card c : hand) {
            c.readCard();
        }
    }
    /**
     * Gets a card from the index given 
     * @param index Position of the wanted card
     * @return The card at the given index
     */
    public Card getCard(int index){
        return (hand.get(index));
    }

    public void emptyHand(){
        hand.clear();
    }
}

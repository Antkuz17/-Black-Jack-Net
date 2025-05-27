
/**
 * The {@code Deck} class represents one or more standard Blackjack decks.
 * It provides methods to shuffle the deck, draw cards, and insert a cut card
 * to simulate real casino play and prevent card counting.
 * <p>
 * Each deck consists of 52 cards, and the number of decks can be specified
 * during instantiation. The class relies on the {@code Card} class to represent
 * individual playing cards.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Iterator;

public class Deck implements Iterable<Card> {
    private ArrayList<Card> deck;
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
     * Allows the Deck to be used in enhanced for-loops.
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
        Card drawnCard = deck.get(0);
        deck.remove(0);
        return drawnCard;
    }

    /**
     * Inserts the cut card between 30% and 75% of the way through the deck.
     * <ul>
     * <li>Prevents full-deck play.</li>
     * <li>Limits advantage from card counting.</li>
     * </ul>
     */
    public void insertCutCard() {
        int deckLength = deck.size();
        int minIndex = (int) (deckLength * 0.3);
        int maxIndex = (int) (deckLength * 0.75);
        int randomIndex = random.nextInt(minIndex, maxIndex + 1);
        deck.add(randomIndex, new Card("cutCard", "cutCard"));
    }
}

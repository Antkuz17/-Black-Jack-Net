import java.util.Scanner;

public class Game {

        public Hand playerHand = new Hand();
        public Hand dealerHand = new Hand();
        private Deck deck;
        private Player player = new Player(playerHand, 1000, "Anton", 0,  0);
        private Dealer dealer = new Dealer(dealerHand);
        public static Scanner input = new Scanner(System.in);
        private boolean gameOver = false;

        public Game() {
                // Initialize deck, player, and dealer
                deck = new Deck(6); // 6 decks
                deck.shuffle(); // Deck is shuffled
                deck.insertCutCard(); // Cut card is added
        }

        public void start() {
                // intro(); // Reads rules
                dealInitialCards(); // deal initial hands (both player and dealer)
                System.out.println("How much would you like to bet on this hand?: ");
                player.placeBet(input.nextInt());
                input.nextLine(); // consumes newline characters
                System.out.println("You have the following cards: ");
                playerHand.showHand();
                System.out.println("The dealers up card is: ");
                (dealerHand.getCard(0)).readCard();
                playerTurn();
                if (gameOver == false){
                        dealerTurn();
                }
                System.out.println("Final Balance: " + player.getBalance());

        }

        /**
         * Deals the initial set of cards to both the player and the dealer.
         * The player and dealer each receive two cards, with the dealer's second card
         * being face down
         * The order of dealing is: player, dealer, dealer, player.
         */
        public void dealInitialCards() {
                playerHand.addCard(deck.draw());
                dealerHand.addCard(deck.draw());
                playerHand.addCard(deck.draw());
                dealerHand.addCard(deck.draw());
                // playerHand.addCard(new Card("Spades", "Ace"));
                // playerHand.addCard(new Card("Spades", "King"));
                
        }

        public void playerTurn() {
                if (playerHand.getTotalValue() == 21) { // Checks if natural blackjack
                        System.out.println("You have natural blackjack");
                        if (dealerHand.getTotalValue() == 21) { // If the dealer also has natural blackjack, the bet is
                                                                // pushed
                                System.out.println("Its a push, dealer also has blackJack");
                                player.pushBet();
                                gameOver = true;
                                return;
                        } else {
                                System.out.println("You win, dealer does not have blackJack");
                                player.naturalBJ();
                                gameOver = true;
                                return;
                        }
                }
                if (dealerHand.getCard(0).getRank().equals("Ace")){ // insurance
                        System.out.println("The dealers up card is an ace");
                        System.out.println("You may buy insurance which is half your bet:");
                        System.out.println("Would you like insurance? (y/n)");
                        String insuranceAnswer = input.nextLine();
                        if (insuranceAnswer.equalsIgnoreCase("y")){
                                player.setInsurance();
                        }
                }

                System.out.println("Would you like to double down (y/n)");
                String doubleDownAnswer = input.nextLine();
                if(doubleDownAnswer.equalsIgnoreCase("y")){
                        player.doubleDown();
                }
                // hit or stand logic
                System.out.println("Your hand has a value of: " + playerHand.getTotalValue());
                System.out.print("Would you like to hit or stand?: (h/s)");
                String choice = input.nextLine();
                while(choice.equalsIgnoreCase("h")){
                        playerHand.addCard(deck.draw());
                        System.out.println("Your hand is: ");
                        playerHand.showHand();
                        if(playerHand.checkIfBust()){
                                System.out.println("You are over 21, you bust and loose");
                                player.looseBet();
                                gameOver = true;
                                return;
                        }
                        System.out.println("Your hand has a value of: " + playerHand.getTotalValue());
                        System.out.print("Would you like to hit or stand?: (h/s)");
                        choice = input.nextLine();
                }

                System.out.println("Your turn is over, now its dealer turn");
        }

        public void dealerTurn(){
                System.out.println("Dealer Turn: ");
                System.out.println("The dealer has the following");
                dealerHand.showHand();
                System.out.print("Value: " + dealerHand.getTotalValue());
                while(dealerHand.getTotalValue() <17){
                        dealerHand.addCard(deck.draw());
                        System.out.println("The dealer has the following");
                        dealerHand.showHand();
                        System.out.print("Value: " + dealerHand.getTotalValue());
                        if(dealerHand.getTotalValue() >21){
                                System.out.println("Dealer busts, you win");
                                player.winBet();
                        }
                }
                if(dealerHand.getTotalValue() > playerHand.getTotalValue()){
                        System.out.println("You lost, boohoo");
                        player.looseBet();
                }
                else{
                        System.out.println("You win, good job");
                        player.winBet();
                }
        }
        /**
         * Asks if the player wants an review on game rules
         */
        public static void intro() {
                System.out.println("Hello User, you are playing BlackJack");
                System.out.print("Would you like a tutorial? (y/n): ");
                String answer = input.nextLine();
                if (answer.equalsIgnoreCase("y")) {
                        System.out.println("Full Blackjack Rules:");
                        System.out.println("1. Each player is dealt 2 cards face up.");
                        System.out.println("2. The dealer gets 2 cards – one face up, one face down.");
                        System.out.println("3. Number cards (2–10) are worth face value.");
                        System.out.println("4. Face cards (J, Q, K) are worth 10.");
                        System.out.println("5. Aces are worth 1 or 11, whichever helps the hand most.");
                        System.out.println("6. The goal is to get as close to 21 as possible without going over.");
                        System.out.println(
                                        "7. An Ace + 10-value card on the first deal is a Blackjack and usually pays 3:2.");
                        System.out.println("8. Players can Hit (take a card) or Stand (end their turn).");
                        System.out.println(
                                        "9. Players can Double Down on their first two cards – double the bet, take only one more card.");
                        System.out.println(
                                        "10. Players can Split if the first two cards are the same rank – creates two hands with separate bets.");
                        System.out.println(
                                        "11. Split Aces usually get only one card each; some casinos limit actions after a split.");
                        System.out.println(
                                        "12. Players can Surrender in some games – forfeit half the bet and fold the hand immediately.");
                        System.out.println("13. Dealer reveals their hidden card after all players finish acting.");
                        System.out.println(
                                        "14. Dealer must hit until 17 or higher (some casinos require hitting on soft 17).");
                        System.out.println("15. If the dealer busts (over 21), all remaining players win.");
                        System.out.println("16. If player and dealer tie, it’s a push (bet returned).");
                        System.out.println(
                                        "17. If the dealer’s face-up card is an Ace, players can buy Insurance – a side bet (half the original bet) that pays 2:1 if the dealer has Blackjack.");
                        System.out.println(
                                        "18. If the player has Blackjack and the dealer shows an Ace, the player may take Even Money – accept a guaranteed 1:1 payout before the dealer checks for Blackjack.");
                        System.out.println("19. Insurance bets lose if the dealer doesn’t have Blackjack.");
                        System.out.println("20. Blackjack beats any other 21 (e.g., three cards totaling 21).");
                        System.out.println("21. Winning non-Blackjack hands pay 1:1.");
                }
                System.out.println("Now begins the game :)");
        }

}

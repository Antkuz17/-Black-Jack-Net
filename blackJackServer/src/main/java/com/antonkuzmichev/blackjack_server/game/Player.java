package com.antonkuzmichev.blackjack_server.game;


// Class represents 1 player and all the associated attributes
public class Player {
    private Hand hand;
    private int balance;
    private String name;
    private int bet;
    private int insurance;
    private boolean hasActed = false; // Track if player has completed their turn 
    
    // Constructor
    public Player(Hand hand, int balance, String name, int bet, int insurance) {
        this.hand = hand;
        this.balance = balance;
        this.name = name;
        this.bet = bet;
        this.insurance = insurance;
    }

    // Getters and setters
    public int getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getCurrentBet() {
        return bet;
    }

    public int getInsurance() {
        return insurance;
    }

    public boolean hasActed() {
        return hasActed;
    }

    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }

    /**
     * Places a bet if the player has sufficient balance
     * 
     * @param amount The amount to bet
     * @return true if bet was placed successfully
     */
    public boolean placeBet(int amount) {
        if (balance < amount || amount <= 0) { // If they dont have enough money or they have no money in the account, return false
            return false;
        }
        bet = amount;
        balance -= bet; // If the bet was successful return true
        return true;
    }

    /**
     * Bet is doubled and returned to the players balance
     */
    public void winBet() {
        balance += (bet * 2);
        bet = 0;
    }

    /**
     * Bet is lost (set to zero)
     */
    public void looseBet() {
        bet = 0;
    }

    /**
     * Bet is returned to the players balance (tie/push)
     */
    public void pushBet() {
        balance += bet;
        bet = 0;
    }

    /**
     * Natural blackjack, pays less than normal win (1.5x and not 2x)
     */
    public void naturalBJ() {
        balance += bet + (bet * 3 / 2);
        bet = 0;
    }

    /**
     * Attempts to buy insurance
     * 
     * @return true if insurance was purchased successfully
     */
    public boolean buyInsurance() {
        int insuranceCost = bet / 2;
        if (balance < insuranceCost) { // If not enough money return false
            return false;
        }
        insurance = insuranceCost;
        balance -= insurance;
        return true; // Otherwise return true
    }

    /**
     * Pay out insurance (2:1 payout)
     */
    public void payInsurance() {
        balance += insurance * 3; // Original insurance bet + 2:1 payout
        insurance = 0;
    }

    /**
     * Lose insurance bet
     */
    public void loseInsurance() {
        insurance = 0;
    }

    /**
     * Double down - doubles the bet and player gets one more card
     * 
     * @return true if double down was successful
     */
    public boolean doubleDown() {
        if (balance < bet) {
            return false;
        }
        balance -= bet;
        bet = bet * 2;
        return true;
    }

    /**
     * Reset for new round: clears hand, bets, but keeps balance
     */
    public void resetRound() {
        bet = 0;
        insurance = 0;
        hasActed = false;
        hand.emptyHand();
    }

    /**
     * Check if player can afford a bet
     */
    public boolean canAfford(int amount) {
        return balance >= amount;
    }

    /**
     * Check if player has natural blackjack
     */
    public boolean hasNaturalBlackjack() {
        return hand.getTotalValue() == 21 && hand.getCardCount() == 2;
    }

    /**
     * Check if player is busted
     */
    public boolean isBusted() {
        return hand.checkIfBust();
    }

    /**
     * Get player status for game state
     * Returns all relevant stats for a player
     * Used in communicating with other clients
     */
    public PlayerStatusDTO getStatusDTO() {
        return new PlayerStatusDTO(
                name,
                balance,
                bet,
                insurance,
                hand.getTotalValue(),
                hand.getCardCount(),
                hasActed,
                isBusted(),
                hasNaturalBlackjack());
    }
}
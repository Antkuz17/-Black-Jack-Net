/*
 * Player has chips, hand, 
 */
import java.util.Scanner;


public class Player {
    private Hand hand;
    private int balance;
    private String name;
    private int bet;
    private int insurance;

    public Scanner input = new Scanner(System.in);



    public Player(Hand hand, int balance, String name, int bet, int insurance){
        this.hand = hand;
        this.balance = balance;
        this.name = name;
        this.bet = bet;
        this.insurance = insurance;
    }

    /*place bet
     * win bet
     * loose bet
     * pushbet
    */
    /**
     * Gives the amount of money that the player has
     * @return int value of the balance the player has
     */
    public int getBalance(){
        return balance;
    }
    /**
     * Allows the player to bet an amount. Checks whether the player has enough in their balance to afford it, if not ask to enter bet again.
     * @param amount The amount the player is trying to bet
     */
    public void placeBet(int amount){
        while(balance < amount){
            System.out.println("You are broke, bet less");
            System.out.print("Enter Bet: ");
            amount = input.nextInt();
        }
        bet += amount;
        balance -=bet;
    }
    /**
     * Bet is doubled and returned to the players balance
     */

    public void winBet(){
        balance += (bet *2);
        bet = 0;
    }

    /**
     * Bet is lost (set to zero)
     */
    public void looseBet(){
        bet = 0;
    }

    /**
     * Bet is returned to the players balance
     */

    public void pushBet(){
        balance += bet;
        bet = 0;
    }
    /**
     * Bet is halved and returned to player (Bet = $10, win $15)
     * Occurs when player has blackjack off first two cards and dealer does not
     */
    public void naturalBJ(){
        balance += bet/2;
        bet = 0;
    }
    /**
     * Checks if the player can afford insurance
     * If they can
    */
    public void setInsurance(){
        if(balance< bet/2){
            System.out.println("You are too broke for insurance");
            return;
        }
        insurance = bet/2;
        balance -= insurance;
    }

    


}

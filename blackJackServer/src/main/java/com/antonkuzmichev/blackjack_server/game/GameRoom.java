package com.antonkuzmichev.blackjack_server.game;

import org.springframework.web.socket.WebSocketSession;
import java.util.*;

public class GameRoom {

    public enum GamePhase {
        WAITING, BETTING, DEALING, PLAYER_TURNS, DEALER_TURN, SHOWDOWN
    }

    private String roomId;
    private Map<WebSocketSession, Player> players = new HashMap<>();
    private List<WebSocketSession> playerOrder = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private WebSocketSession host;
    private Deck deck;
    private Hand dealerHand;
    private GamePhase currentPhase = GamePhase.WAITING;

    public GameRoom(String roomId, WebSocketSession host) {
        this.roomId = roomId;
        this.host = host;
        this.deck = new Deck(6);
        this.deck.shuffle();
        this.dealerHand = new Hand();
    }

    public void addPlayer(WebSocketSession session, Player player) {
        players.put(session, player);
        playerOrder.add(session);
    }

    public void removePlayer(WebSocketSession session) {
        players.remove(session);
        playerOrder.remove(session);

        // Adjust current player index if necessary
        if (currentPlayerIndex >= playerOrder.size()) {
            currentPlayerIndex = Math.max(0, playerOrder.size() - 1);
        }
    }

    // Getters and setters
    public String getRoomId() {
        return roomId;
    }

    public Map<WebSocketSession, Player> getPlayers() {
        return players;
    }

    public List<WebSocketSession> getPlayerOrder() {
        return playerOrder;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public WebSocketSession getHost() {
        return host;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        this.currentPhase = phase;
    }
}
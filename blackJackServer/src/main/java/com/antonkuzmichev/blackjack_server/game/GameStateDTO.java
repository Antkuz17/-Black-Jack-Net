package com.antonkuzmichev.blackjack_server.game;

import org.springframework.web.socket.WebSocketSession;
import java.util.*;

// Class used in communicaiton with the frontend. 
// Communicates all game information such as phase of game, number of players, the order, dealer stats, hostID, etc

public class GameStateDTO {
    private String roomId;
    private GameRoom.GamePhase currentPhase; // (Betting, dealing, showdown, etc)
    private Map<String, PlayerStatusDTO> players; // Maps players to their websocket connections
    private List<String> playerOrder;
    private String currentPlayerId; // ID of current player going
    private List<CardDTO> dealerCards;
    private int dealerValue;
    private String hostId;
    private boolean dealerSecondCardHidden; // Depends on the game state (showdown ---> true)

    // Constructor
    public GameStateDTO(String roomId, GameRoom.GamePhase currentPhase,
            Map<WebSocketSession, Player> playerMap,
            List<WebSocketSession> playerOrderSessions,
            int currentPlayerIndex, Hand dealerHand, String hostSessionId) {
        this.roomId = roomId;
        this.currentPhase = currentPhase;
        this.hostId = hostSessionId;

        
        this.players = new HashMap<>(); // Create a hashmap using the Map interface inilizied before
        for (Map.Entry<WebSocketSession, Player> entry : playerMap.entrySet()) { // for each player
            String sessionId = entry.getKey().getId(); // get the id
            PlayerStatusDTO playerStatus = entry.getValue().getStatusDTO();
            playerStatus.setSessionId(sessionId); // Make a playerstatusDTO object and match with the ID

            // Add player's cards to the card DTO
            // Take the hand of the player and convert it to a set of CardDTO objects
            List<CardDTO> playerCards = new ArrayList<>();
            Hand playerHand = entry.getValue().getHand();
            for (int i = 0; i < playerHand.getCardCount(); i++) {
                Card card = playerHand.getCard(i);
                playerCards.add(new CardDTO(card.getSuit(), card.getRank()));
            }
            playerStatus.setCards(playerCards);
            // Set the list of playerCards as CardDTO objects

            this.players.put(sessionId, playerStatus);
        }

        // Convert player order
        this.playerOrder = new ArrayList<>(); // Stores player order by their ID
        for (WebSocketSession session : playerOrderSessions) {
            this.playerOrder.add(session.getId());
        }

        // Set current player
        // Determine whose turn it is by getting the current player's session ID
        if (!playerOrderSessions.isEmpty() && currentPlayerIndex < playerOrderSessions.size()) {
            this.currentPlayerId = playerOrderSessions.get(currentPlayerIndex).getId();
        }

        // Set dealer info
        // Second card of dealer is hidden during betting, dealing, player_turns
        this.dealerCards = new ArrayList<>();
        this.dealerSecondCardHidden = (currentPhase == GameRoom.GamePhase.BETTING ||
                currentPhase == GameRoom.GamePhase.DEALING ||
                currentPhase == GameRoom.GamePhase.PLAYER_TURNS);

        
        // Converts dealers hand into cartDTO objects
        for (int i = 0; i < dealerHand.getCardCount(); i++) {
            Card card = dealerHand.getCard(i);
            if (i == 1 && dealerSecondCardHidden) {
                this.dealerCards.add(new CardDTO("Hidden", "Hidden"));
            } else {
                this.dealerCards.add(new CardDTO(card.getSuit(), card.getRank()));
            }
        }

        this.dealerValue = dealerSecondCardHidden ? -1 : dealerHand.getTotalValue();
    }

    // Getters
    public String getRoomId() {
        return roomId;
    }

    public GameRoom.GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public Map<String, PlayerStatusDTO> getPlayers() {
        return players;
    }

    public List<String> getPlayerOrder() {
        return playerOrder;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public List<CardDTO> getDealerCards() {
        return dealerCards;
    }

    public int getDealerValue() {
        return dealerValue;
    }

    public String getHostId() {
        return hostId;
    }

    public boolean isDealerSecondCardHidden() {
        return dealerSecondCardHidden;
    }
}
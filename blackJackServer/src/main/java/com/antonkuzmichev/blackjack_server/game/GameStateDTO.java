package com.antonkuzmichev.blackjack_server.game;

import org.springframework.web.socket.WebSocketSession;
import java.util.*;

public class GameStateDTO {
    private String roomId;
    private GameRoom.GamePhase currentPhase;
    private Map<String, PlayerStatusDTO> players;
    private List<String> playerOrder;
    private String currentPlayerId;
    private List<CardDTO> dealerCards;
    private int dealerValue;
    private String hostId;
    private boolean dealerSecondCardHidden;

    public GameStateDTO(String roomId, GameRoom.GamePhase currentPhase,
            Map<WebSocketSession, Player> playerMap,
            List<WebSocketSession> playerOrderSessions,
            int currentPlayerIndex, Hand dealerHand, String hostSessionId) {
        this.roomId = roomId;
        this.currentPhase = currentPhase;
        this.hostId = hostSessionId;

        // Convert players map
        this.players = new HashMap<>();
        for (Map.Entry<WebSocketSession, Player> entry : playerMap.entrySet()) {
            String sessionId = entry.getKey().getId();
            PlayerStatusDTO playerStatus = entry.getValue().getStatusDTO();
            playerStatus.setSessionId(sessionId);

            // Add player's cards to the DTO
            List<CardDTO> playerCards = new ArrayList<>();
            Hand playerHand = entry.getValue().getHand();
            for (int i = 0; i < playerHand.getCardCount(); i++) {
                Card card = playerHand.getCard(i);
                playerCards.add(new CardDTO(card.getSuit(), card.getRank()));
            }
            playerStatus.setCards(playerCards);

            this.players.put(sessionId, playerStatus);
        }

        // Convert player order
        this.playerOrder = new ArrayList<>();
        for (WebSocketSession session : playerOrderSessions) {
            this.playerOrder.add(session.getId());
        }

        // Set current player
        if (!playerOrderSessions.isEmpty() && currentPlayerIndex < playerOrderSessions.size()) {
            this.currentPlayerId = playerOrderSessions.get(currentPlayerIndex).getId();
        }

        // Set dealer info
        this.dealerCards = new ArrayList<>();
        this.dealerSecondCardHidden = (currentPhase == GameRoom.GamePhase.BETTING ||
                currentPhase == GameRoom.GamePhase.DEALING ||
                currentPhase == GameRoom.GamePhase.PLAYER_TURNS);

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
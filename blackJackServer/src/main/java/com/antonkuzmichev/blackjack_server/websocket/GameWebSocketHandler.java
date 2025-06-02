package com.antonkuzmichev.blackjack_server.websocket;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.antonkuzmichev.blackjack_server.game.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>(); // Thread safe map implementation allowing multi-threading
    private final Map<WebSocketSession, String> sessionToRoom = new ConcurrentHashMap<>(); // Important to avoid corruption since multiple clients may write at one time
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson library used to convert from java object to json

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        System.out.println("New WebSocket connection: " + session.getId());
        sendMessage(session, createMessage("connection", "Connected successfully"));
    }
    // Sends confirmation message to both server and client that connection is successful

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) { //Clean up player data
        String roomId = sessionToRoom.get(session);
        if (roomId != null) {
            GameRoom room = gameRooms.get(roomId);
            if (room != null) {
                room.removePlayer(session); // remove player from room
                broadcastGameState(room); // broadcast to other players that someone disconnected

                if (room.getPlayers().isEmpty()) {
                    gameRooms.remove(roomId);
                } // If the room is now empty, remove the room as well
            }
            sessionToRoom.remove(session);
        }
        System.out.println("Connection closed: " + session.getId());
        // Send confirmaiton to server
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message)
            throws IOException { // Parses client message from json into a client message object
        try {
            ClientMessage clientMessage = objectMapper.readValue(message.getPayload(), ClientMessage.class);
            handleClientMessage(session, clientMessage);
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            sendMessage(session, createMessage("error", "Invalid message format"));
        }
    }

    private void handleClientMessage(WebSocketSession session, ClientMessage message) throws IOException {
        String messageType = message.getType(); // Depending on the clients message do different things like bet deal etc

        switch (messageType) {
            case "createRoom":
                createRoom(session, message.getPlayerName());
                break;
            case "joinRoom":
                joinRoom(session, message.getPlayerName(), message.getRoomId());
                break;
            case "bet":
                placeBet(session, message.getAmount());
                break;
            case "hit":
                playerHit(session);
                break;
            case "stand":
                playerStand(session);
                break;
            case "doubleDown":
                playerDoubleDown(session);
                break;
            case "startGame":
                startGame(session);
                break;
            case "newRound":
                startNewRound(session);
                break;
            default:
                sendMessage(session, createMessage("error", "Unknown message type: " + messageType));
        }
    }

    private void createRoom(WebSocketSession session, String playerName) throws IOException { // Creates new room
        String roomId = UUID.randomUUID().toString().substring(0, 8); // Generate a random number for ID
        Player host = new Player(new Hand(), 1000, playerName != null ? playerName : "Host", 0, 0);
        // Makes a host and addes the host to the gameroom
        GameRoom room = new GameRoom(roomId, session);
        room.addPlayer(session, host);

        gameRooms.put(roomId, room);
        sessionToRoom.put(session, roomId);

        sendMessage(session, createMessage("roomCreated", roomId)); // Send confirmation message
        broadcastGameState(room); // Broadcast the new room to everyone
    }

    private void joinRoom(WebSocketSession session, String playerName, String roomId) throws IOException {
        if (roomId == null) { // If no room id was entered give an error
            sendMessage(session, createMessage("error", "No room ID provided"));
            return;
        }

        GameRoom room = gameRooms.get(roomId);
        if (room == null) {
            sendMessage(session, createMessage("error", "Room not found"));
            return;
        }

        if (room.getPlayers().size() >= 6) { // If more than 6 people give error
            sendMessage(session, createMessage("error", "Room is full"));
            return;
        }

        Player player = new Player(new Hand(), 1000, playerName != null ? playerName : "Player", 0, 0);
        room.addPlayer(session, player); // Add the player to the room
        sessionToRoom.put(session, roomId);

        sendMessage(session, createMessage("joinedRoom", roomId)); 
        broadcastGameState(room); // broadcast the new player to everyone
    }

    private void startGame(WebSocketSession session) throws IOException {
        GameRoom room = getRoomForSession(session);
        if (room == null || !session.equals(room.getHost())) {
            sendMessage(session, createMessage("error", "Only host can start the game"));
            return;
        }

        if (room.getCurrentPhase() != GameRoom.GamePhase.WAITING) {
            sendMessage(session, createMessage("error", "Game already in progress"));
            return;
        }

        room.setCurrentPhase(GameRoom.GamePhase.BETTING);
        broadcastGameState(room);
        broadcastMessage(room, createMessage("info", "Betting phase started! Place your bets."));
    }

    private void placeBet(WebSocketSession session, Integer amount) throws IOException {
        GameRoom room = getRoomForSession(session);
        if (room == null || amount == null)
            return;

        if (room.getCurrentPhase() != GameRoom.GamePhase.BETTING) {
            sendMessage(session, createMessage("error", "Not in betting phase"));
            return;
        }

        Player player = room.getPlayers().get(session);
        if (player == null)
            return;

        if (player.placeBet(amount)) {
            sendMessage(session, createMessage("betPlaced", amount));

            // Check if all players have bet
            boolean allBetsPlaced = room.getPlayers().values().stream()
                    .allMatch(p -> p.getCurrentBet() > 0);

            if (allBetsPlaced) {
                dealInitialCards(room);
            } else {
                broadcastGameState(room);
            }
        } else {
            sendMessage(session, createMessage("error", "Insufficient balance or invalid bet"));
        }
    }

    private void dealInitialCards(GameRoom room) throws IOException {
        room.setCurrentPhase(GameRoom.GamePhase.DEALING);

        // Deal two cards to each player and dealer
        for (int i = 0; i < 2; i++) {
            for (WebSocketSession playerSession : room.getPlayerOrder()) {
                Player player = room.getPlayers().get(playerSession);
                player.getHand().addCard(room.getDeck().draw());
            }
            room.getDealerHand().addCard(room.getDeck().draw());
        }

        // Check for natural blackjacks
        boolean dealerHasBlackjack = room.getDealerHand().getTotalValue() == 21;

        for (Map.Entry<WebSocketSession, Player> entry : room.getPlayers().entrySet()) {
            Player player = entry.getValue();
            if (player.hasNaturalBlackjack()) {
                if (dealerHasBlackjack) {
                    player.pushBet(); // Push
                } else {
                    player.naturalBJ(); // Natural blackjack payout
                }
                player.setHasActed(true);
            }
        }

        if (dealerHasBlackjack) {
            // Dealer has blackjack, resolve all bets
            for (Player player : room.getPlayers().values()) {
                if (!player.hasNaturalBlackjack()) {
                    player.looseBet();
                }
            }
            room.setCurrentPhase(GameRoom.GamePhase.SHOWDOWN);
        } else {
            // Start player turns
            room.setCurrentPhase(GameRoom.GamePhase.PLAYER_TURNS);
            room.setCurrentPlayerIndex(0);
        }

        broadcastGameState(room);
    }

    private void playerHit(WebSocketSession session) throws IOException {
        GameRoom room = getRoomForSession(session);
        if (room == null || room.getCurrentPhase() != GameRoom.GamePhase.PLAYER_TURNS) {
            return;
        }

        if (!isCurrentPlayer(session, room)) {
            sendMessage(session, createMessage("error", "Not your turn"));
            return;
        }

        Player player = room.getPlayers().get(session);
        player.getHand().addCard(room.getDeck().draw());

        if (player.isBusted()) {
            player.setHasActed(true);
            player.looseBet();
            nextPlayer(room);
        }

        broadcastGameState(room);
    }

    private void playerStand(WebSocketSession session) throws IOException {
        GameRoom room = getRoomForSession(session);
        if (room == null || room.getCurrentPhase() != GameRoom.GamePhase.PLAYER_TURNS) {
            return;
        }

        if (!isCurrentPlayer(session, room)) {
            sendMessage(session, createMessage("error", "Not your turn"));
            return;
        }

        Player player = room.getPlayers().get(session);
        player.setHasActed(true);
        nextPlayer(room);
        broadcastGameState(room);
    }

    private void playerDoubleDown(WebSocketSession session) throws IOException {
        GameRoom room = getRoomForSession(session);
        if (room == null || room.getCurrentPhase() != GameRoom.GamePhase.PLAYER_TURNS) {
            return;
        }

        if (!isCurrentPlayer(session, room)) {
            sendMessage(session, createMessage("error", "Not your turn"));
            return;
        }

        Player player = room.getPlayers().get(session);

        if (player.getHand().getCardCount() != 2) {
            sendMessage(session, createMessage("error", "Can only double down on first two cards"));
            return;
        }

        if (player.doubleDown()) {
            player.getHand().addCard(room.getDeck().draw());
            player.setHasActed(true);

            if (player.isBusted()) {
                player.looseBet();
            }

            nextPlayer(room);
            broadcastGameState(room);
        } else {
            sendMessage(session, createMessage("error", "Insufficient balance to double down"));
        }
    }

    private void nextPlayer(GameRoom room) throws IOException {
        int nextIndex = room.getCurrentPlayerIndex() + 1;

        // Find next player who hasn't acted
        while (nextIndex < room.getPlayerOrder().size()) {
            WebSocketSession nextSession = room.getPlayerOrder().get(nextIndex);
            Player nextPlayer = room.getPlayers().get(nextSession);

            if (!nextPlayer.hasActed() && !nextPlayer.isBusted() && !nextPlayer.hasNaturalBlackjack()) {
                room.setCurrentPlayerIndex(nextIndex);
                return;
            }
            nextIndex++;
        }

        // All players have acted, start dealer turn
        dealerTurn(room);
    }

    private void dealerTurn(GameRoom room) throws IOException {
        room.setCurrentPhase(GameRoom.GamePhase.DEALER_TURN);

        // Dealer draws until 17 or higher
        while (room.getDealerHand().getTotalValue() < 17) {
            room.getDealerHand().addCard(room.getDeck().draw());
        }

        resolveRound(room);
    }

    private void resolveRound(GameRoom room) throws IOException {
        room.setCurrentPhase(GameRoom.GamePhase.SHOWDOWN);
        int dealerValue = room.getDealerHand().getTotalValue();
        boolean dealerBusted = dealerValue > 21;

        for (Player player : room.getPlayers().values()) {
            if (player.getCurrentBet() == 0)
                continue; // Already resolved (blackjack/bust)

            int playerValue = player.getHand().getTotalValue();

            if (player.isBusted()) {
                // Already handled in hit method
                continue;
            } else if (dealerBusted || playerValue > dealerValue) {
                player.winBet();
            } else if (playerValue == dealerValue) {
                player.pushBet();
            } else {
                player.looseBet();
            }
        }

        broadcastGameState(room);
        broadcastMessage(room, createMessage("info", "Round complete! Results updated."));
    }

    private void startNewRound(WebSocketSession session) throws IOException {
        GameRoom room = getRoomForSession(session);
        if (room == null || !session.equals(room.getHost())) {
            sendMessage(session, createMessage("error", "Only host can start new round"));
            return;
        }

        if (room.getCurrentPhase() != GameRoom.GamePhase.SHOWDOWN) {
            sendMessage(session, createMessage("error", "Round not finished"));
            return;
        }

        // Reset all players for new round
        for (Player player : room.getPlayers().values()) {
            player.resetRound();
        }

        room.getDealerHand().emptyHand();
        room.setCurrentPlayerIndex(0);
        room.setCurrentPhase(GameRoom.GamePhase.BETTING);

        // Shuffle deck if running low
        if (room.getDeck().size() < 20) {
            room.setDeck(new Deck(6));
            room.getDeck().shuffle();
        }

        broadcastGameState(room);
        broadcastMessage(room, createMessage("info", "New round started! Place your bets."));
    }

    // Helper methods
    private GameRoom getRoomForSession(WebSocketSession session) {
        String roomId = sessionToRoom.get(session);
        return roomId != null ? gameRooms.get(roomId) : null;
    }

    private boolean isCurrentPlayer(WebSocketSession session, GameRoom room) {
        if (room.getCurrentPlayerIndex() >= room.getPlayerOrder().size()) {
            return false;
        }
        return session.equals(room.getPlayerOrder().get(room.getCurrentPlayerIndex()));
    }

    private void broadcastGameState(GameRoom room) {
        GameStateDTO gameState = new GameStateDTO(
                room.getRoomId(),
                room.getCurrentPhase(),
                room.getPlayers(),
                room.getPlayerOrder(),
                room.getCurrentPlayerIndex(),
                room.getDealerHand(),
                room.getHost().getId());

        String message;
        try {
            message = objectMapper.writeValueAsString(createMessage("gameState", gameState));
        } catch (IOException e) {
            System.err.println("Error serializing game state: " + e.getMessage());
            return; // can't send anything if serialization failed
        }

        for (WebSocketSession session : room.getPlayers().keySet()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("Error broadcasting to session: " + e.getMessage());

                }
            }
        }
    }    

    private void broadcastMessage(GameRoom room, Map<String, Object> message) throws IOException {
        String messageJson = objectMapper.writeValueAsString(message);

        for (WebSocketSession session : room.getPlayers().keySet()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(messageJson));
                } catch (IOException e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                }
            }
        }
    }

    private void sendMessage(WebSocketSession session, Map<String, Object> message) throws IOException {
        if (session.isOpen()) {
            String messageJson = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(messageJson));
        }
    }

    private Map<String, Object> createMessage(String type, Object data) {
        return Map.of("type", type, "data", data);
    }
}
package com.antonkuzmichev.blackjack_server.websocket;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// GameWebSocketHandler.java
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketSession, String> playerNames = new ConcurrentHashMap<>();
    private int playerCounter = 1;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
        String playerName = "Player " + playerCounter++;
        playerNames.put(session, playerName);
        System.out.println("New WebSocket connection: " + playerName + " (" + session.getId() + ")");
        broadcast(playerName + " joined the game.");
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String playerName = playerNames.getOrDefault(session, "A player");
        sessions.remove(session);
        playerNames.remove(session);
        System.out.println("Connection closed: " + playerName + " (" + session.getId() + ")");
        broadcast(playerName + " left the game.");
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message)
            throws IOException {
        String playerName = playerNames.get(session);
        String fullMessage = playerName + ": " + message.getPayload();
        System.out.println("Received: " + fullMessage);
        broadcast(fullMessage);
    }

    private void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.antonkuzmichev.blackjack_server.game;

public class ClientMessage {
    private String type;
    private String playerName;
    private String roomId;
    private Integer amount;

    // Default constructor
    public ClientMessage() {
    }

    // Constructor with parameters
    public ClientMessage(String type, String playerName, String roomId, Integer amount) {
        this.type = type;
        this.playerName = playerName;
        this.roomId = roomId;
        this.amount = amount;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
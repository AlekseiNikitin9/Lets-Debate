package com.debateapp.dto;

public class PlayerJoinedEvent {
    private String type = "PLAYER_JOINED";
    private String playerName;
    private String gameCode;

    public PlayerJoinedEvent(String playerName, String gameCode) {
        this.playerName = playerName;
        this.gameCode = gameCode;
    }

    public String getType() {
        return type;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getGameCode() {
        return gameCode;
    }
}

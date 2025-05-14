package com.debateapp.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class ChatRoom {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String gameCode;

    private String player1Name;
    private String player2Name;

    private int p1RefCalls = 0;
    private int p2RefCalls = 0;

    @Lob
    private String chatHistory = "";

    public ChatRoom() {}

    // Getters and Setters

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }


    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }


    public int getP1RefCalls() {
        return p1RefCalls;
    }

    public void setP1RefCalls(int p1RefCalls) {
        this.p1RefCalls = p1RefCalls;
    }

    public int getP2RefCalls() {
        return p2RefCalls;
    }

    public void setP2RefCalls(int p2RefCalls) {
        this.p2RefCalls = p2RefCalls;
    }

    public String getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(String chatHistory) {
        this.chatHistory = chatHistory;
    }
}

package com.debateapp.dto;

public class ChatMessage {
    private String sender;
    private String text;
    private String gameCode;

    public ChatMessage() {}

    public ChatMessage(String sender, String text, String gameCode) {
        this.sender = sender;
        this.text = text;
        this.gameCode = gameCode;
    }

    public String getSender()   { return sender; }
    public String getText()     { return text; }
    public String getGameCode() { return gameCode; }

    public void setSender(String sender)     { this.sender = sender; }
    public void setText(String text)         { this.text = text; }
    public void setGameCode(String gameCode) { this.gameCode = gameCode; }
}

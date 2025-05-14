package com.debateapp.dto;

public class TopicVote {
    private String gameCode;
    private String playerName;
    private int topicIndex;
    private String vote; // "agree" or "disagree"

    public String getGameCode() {
        return gameCode;
    }
    public Integer getTopicIndex() {
        return topicIndex;
    }
    public String getPlayerName() {
        return playerName;
    }
    public String getVote() {
        return vote;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setTopicIndex(int topicIndex) {
        this.topicIndex = topicIndex;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}


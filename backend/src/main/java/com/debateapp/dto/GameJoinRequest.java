package com.debateapp.dto;

public class GameJoinRequest {
    private String name;

    public GameJoinRequest() {}

    public GameJoinRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.debateapp.dto;

public class GameCreateRequest {
    private String name;

    public GameCreateRequest() {}
    public GameCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.debateapp.dto;

public class GameResponse {
    private String code;
    private String status;

    public GameResponse(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}

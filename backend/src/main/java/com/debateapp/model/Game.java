package com.debateapp.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Game {

    private String player1;
    private String player2;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;




    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String code; // 6-digit code like "784123"

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Game() {}

    public Game(String code) {
        this.code = code;
        this.status = GameStatus.CREATED;
    }


    // getters and setters
    public String getPlayer1() {
        return player1;
    }
    public String getPlayer2() {
        return player2;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}

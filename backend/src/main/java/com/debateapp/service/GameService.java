package com.debateapp.service;

import com.debateapp.dto.ChatMessage;
import com.debateapp.model.ChatRoom;
import com.debateapp.model.Game;
import com.debateapp.model.GameStatus;
import com.debateapp.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    private final Random random = new Random();

    public Game createGame(String creatorName) {
        String code = generateUniqueCode();
        Game game = new Game(code);
        game.setPlayer1(creatorName);
        return gameRepository.save(game);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1_000_000));
        } while (gameRepository.existsByCode(code));
        return code;
    }

    public void deleteGame(String code) {
        gameRepository.findByCode(code).ifPresent(gameRepository::delete);
    }

    public void appendMessage(String gameCode, ChatMessage msg) {
        Game game = gameRepository.findByCode(gameCode)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameCode));

        ChatRoom room = game.getChatRoom();
        String existing = room.getChatHistory() == null ? "" : room.getChatHistory();
        String updated = existing + msg.getSender() + ": " + msg.getText() + "\n";

        room.setChatHistory(updated);
        gameRepository.save(game); // persist with cascade
    }



}

package com.debateapp.controller;
import com.debateapp.dto.GameResponse;
import jakarta.servlet.http.HttpServletRequest;

import com.debateapp.dto.GameCreateRequest;
import com.debateapp.dto.GameJoinRequest;
import com.debateapp.dto.PlayerJoinedEvent;
import com.debateapp.model.ChatRoom;
import com.debateapp.model.Game;
import com.debateapp.model.GameStatus;
import com.debateapp.repository.GameRepository;
import com.debateapp.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/game")
public class GameController {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;


    @PostMapping("/create")
    public GameResponse createGame(@RequestBody GameCreateRequest request, HttpServletRequest httpRequest) {
        System.out.println("Request origin: " + httpRequest.getHeader("Origin"));
        System.out.println(request.getName());

        Game game = gameService.createGame(request.getName());
        return new GameResponse(game.getCode(), game.getStatus().toString());
    }

    @PostMapping("/{code}/join")
    public ResponseEntity<?> joinGame(@PathVariable String code, @RequestBody GameJoinRequest request) {
        Optional<Game> optionalGame = gameRepository.findByCode(code);

        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
        }

        Game game = optionalGame.get();

        if (game.getPlayer1() == null) {
            game.setPlayer1(request.getName());
        } else if (game.getPlayer2() == null) {
            game.setPlayer2(request.getName());

            // ✅ Broadcast WebSocket event to /topic/{code}
            PlayerJoinedEvent event = new PlayerJoinedEvent(request.getName(), game.getCode());
            //messagingTemplate.convertAndSend("/topic/" + code, event);

            game.setStatus(GameStatus.IN_PROGRESS);
            game.setChatRoom(new ChatRoom());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game is full");
        }

        gameRepository.save(game);
        return ResponseEntity.ok("Joined game");
    }



    @DeleteMapping("/{code}")
    public void cancelGame(@PathVariable String code) {
        gameService.deleteGame(code);
    }
}


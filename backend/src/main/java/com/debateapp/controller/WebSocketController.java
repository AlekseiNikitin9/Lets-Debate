package com.debateapp.controller;

import com.debateapp.dto.*;
import com.debateapp.model.ChatRoom;
import com.debateapp.model.Game;
import com.debateapp.repository.GameRepository;
import com.debateapp.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, Map<Integer, Map<String, String>>> voteStore = new ConcurrentHashMap<>();

    @Autowired
    private TopicService topicService;

    @MessageMapping("/joined")
    public void handlePlayerJoined(PlayerJoinedEvent event) {
        System.out.println("[JOINED] Player " + event.getPlayerName() + " joined game " + event.getGameCode());
        messagingTemplate.convertAndSend("/waiting/" + event.getGameCode(), event);
    }

    @MessageMapping("/start-topics")
    public void startTopics(PlayerJoinedEvent event) throws Exception {
        List<String> topics;

        if (topicService.topicsExist(event.getGameCode())) {
            topics = topicService.getTopics(event.getGameCode());
            System.out.println("[START-TOPICS] Re-sending cached topics for game " + event.getGameCode());
        } else {
            topics = topicService.generateTopics(event.getGameCode());
            System.out.println("[START-TOPICS] Generated new topics for game " + event.getGameCode() + ": " + topics);
        }

        TopicSyncEvent payload = new TopicSyncEvent(topics);

        // 👇 DEBUG: Log exact JSON
        System.out.println("[DEBUG] Sending TopicSyncEvent: " + new ObjectMapper().writeValueAsString(payload));

        messagingTemplate.convertAndSend("/topic/" + event.getGameCode(), payload);
    }


    @MessageMapping("/vote")
    public void handleVote(TopicVote vote) {
        System.out.println("[VOTE] Player " + vote.getPlayerName() + " voted '" + vote.getVote()
                + "' on topic index " + vote.getTopicIndex() + " in game " + vote.getGameCode());

        voteStore.putIfAbsent(vote.getGameCode(), new ConcurrentHashMap<>());
        var gameVotes = voteStore.get(vote.getGameCode());

        gameVotes.putIfAbsent(vote.getTopicIndex(), new ConcurrentHashMap<>());
        var topicVotes = gameVotes.get(vote.getTopicIndex());

        topicVotes.put(vote.getPlayerName(), vote.getVote());

        System.out.println("[VOTE] Current vote state for topic " + vote.getTopicIndex() + ": " + topicVotes);

        if (topicVotes.size() == 2) {
            System.out.println("[VOTE-COMPLETE] Both players have voted on topic " + vote.getTopicIndex()
                    + " in game " + vote.getGameCode() + ". Broadcasting topic-complete.");
            Collection<String> votes = topicVotes.values();
            boolean disagree = new HashSet<>(votes).size() > 1;

            System.out.println("[DEBUG] topicIndex=" + vote.getTopicIndex() + ", votes=" + votes + ", disagree=" + disagree);

            TopicResultEvent result = new TopicResultEvent(vote.getTopicIndex(), disagree);

            messagingTemplate.convertAndSend("/topic/" + vote.getGameCode() + "/topic-complete", result);

        }
    }
    @MessageMapping("/chat")
    public void handleChat(ChatMessage message) {
        String code = message.getGameCode();
        String fullLine = message.getSender() + ": " + message.getText();

        // Append to DB
        Game game = gameRepository.findByCode(code).orElseThrow();
        ChatRoom room = game.getChatRoom();

        String history = room.getChatHistory();
        room.setChatHistory((history == null ? "" : (history + "\n")) + fullLine);

        gameRepository.save(game); // cascade saves chatRoom too

        // Broadcast to clients
        messagingTemplate.convertAndSend("/topic/" + code + "/chat", message);
    }
}

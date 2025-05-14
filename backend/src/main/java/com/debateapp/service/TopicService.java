package com.debateapp.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TopicService {

    private final Map<String, List<String>> topicMap = new ConcurrentHashMap<>();

    public List<String> generateTopics(String gameCode) {
        List<String> topics = List.of(
                "AI will replace teachers",
                "Pineapple belongs on pizza",
                "Cats are better than dogs"
        );
        topicMap.put(gameCode, topics);
        return topics;
    }

    public List<String> getTopics(String gameCode) {
        return topicMap.get(gameCode);
    }

    public boolean topicsExist(String gameCode) {
        return topicMap.containsKey(gameCode);
    }
}

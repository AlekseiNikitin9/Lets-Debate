package com.debateapp.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TopicService {

    private final Map<String, List<String>> topicMap = new ConcurrentHashMap<>();

    public List<String> generateTopics(String gameCode) {
        List<String> topics = List.of(
                "AI will replace teachers",
                "Pineapple belongs on pizza",
                "Cats are better than dogs",
                "Cereal is a soup",
                "Aliens already live among us — they just work in customer service",
                "Toilet paper should roll under, not over",
                "A hot dog is a sandwich",
                "Time travel should be illegal",
                "Birds aren’t real — they’re government drones",
                "Everyone should walk barefoot for better brain function",
                "Books are obsolete — TikToks are the new literature",
                "We should replace all car horns with laughter sounds",
                "Bananas are nature’s most dangerous weapon",
                "Wearing socks to bed is a criminal offense",
                "Sharks would make great emotional support animals",
                "We should all speak only in rhymes",
                "Left-handed people are plotting something",
                "The moon landing was faked — by cats"
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

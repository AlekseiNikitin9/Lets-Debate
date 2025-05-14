package com.debateapp.dto;

import java.util.List;

public class TopicSyncEvent {
    private String type = "TOPIC_SYNC";
    private List<String> topics;

    // Required for Jackson
    public TopicSyncEvent() {}

    public TopicSyncEvent(List<String> topics) {
        this.topics = topics;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}

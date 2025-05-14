package com.debateapp.dto;

public class TopicResultEvent {
    private int topicIndex;
    private boolean disagree;

    public TopicResultEvent() {}

    public TopicResultEvent(int topicIndex, boolean disagree) {
        this.topicIndex = topicIndex;
        this.disagree = disagree;
    }

    public int getTopicIndex() {
        return topicIndex;
    }

    public boolean isDisagree() {
        return disagree;
    }

    public void setTopicIndex(int topicIndex) {
        this.topicIndex = topicIndex;
    }

    public void setDisagree(boolean disagree) {
        this.disagree = disagree;
    }
}

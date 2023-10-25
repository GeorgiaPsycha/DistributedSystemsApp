package com.example.instasent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Topic implements Serializable {
    private static final long serialVersionUID = -1L;
    private String topic_name;
    private List<String> history;
    private int portNumber;


    public Topic(String topic_name) {
        this.topic_name = topic_name;
        this.history = new ArrayList<>();
    }

    public Topic(String topic_name, int portNumber) {
        this.topic_name = topic_name;
        this.portNumber = portNumber;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topic_name='" + topic_name + '\'' +
                ", portNumber=" + portNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(topic_name, topic.topic_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic_name);
    }
}

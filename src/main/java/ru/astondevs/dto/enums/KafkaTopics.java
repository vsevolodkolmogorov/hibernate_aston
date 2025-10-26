package ru.astondevs.dto.enums;

public enum KafkaTopics {
    USER_EVENT("user.event");

    public final String name;

    KafkaTopics(String name) { this.name = name; }
}

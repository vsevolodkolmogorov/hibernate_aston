package ru.astondevs.dto.enums;

public enum KafkaTopics {
    USER_CREATED("user.created"),
    USER_DELETED("user.deleted");

    public final String name;
    KafkaTopics(String name) { this.name = name; }
}

package ru.astondevs.errors;

public class UserNotFoundedException extends RuntimeException {
    public UserNotFoundedException(String message) {
        super(message);
    }
}

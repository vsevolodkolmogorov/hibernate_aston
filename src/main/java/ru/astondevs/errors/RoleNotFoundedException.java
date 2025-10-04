package ru.astondevs.errors;

public class RoleNotFoundedException extends RuntimeException{

    public RoleNotFoundedException(String message) {
        super(message);
    }
}

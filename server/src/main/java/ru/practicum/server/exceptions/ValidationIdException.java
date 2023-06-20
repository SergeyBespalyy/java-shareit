package ru.practicum.server.exceptions;

public class ValidationIdException extends RuntimeException {
    public ValidationIdException(String message) {
        super(message);
    }
}

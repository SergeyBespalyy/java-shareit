package ru.practicum.gateway.exceptions;

public class ValidationIdException extends RuntimeException {
    public ValidationIdException(String message) {
        super(message);
    }
}

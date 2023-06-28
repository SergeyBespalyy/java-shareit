package ru.practicum.server.exceptions;

public class ItemIsNotAvailableForBookingException extends RuntimeException {
    public ItemIsNotAvailableForBookingException(String s) {
        super(s);
    }
}

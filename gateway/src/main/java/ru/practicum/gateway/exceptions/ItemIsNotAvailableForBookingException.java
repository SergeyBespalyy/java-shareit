package ru.practicum.gateway.exceptions;

public class ItemIsNotAvailableForBookingException extends RuntimeException {
    public ItemIsNotAvailableForBookingException(String s) {
        super(s);
    }
}

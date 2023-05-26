package ru.practicum.shareit.exceptions;

public class ItemIsNotAvailableForBookingException extends RuntimeException {
    public ItemIsNotAvailableForBookingException(String s) {
        super(s);
    }
}

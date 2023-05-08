package ru.practicum.shareit.exceptions;

/**
 * Класс описывающий модель ответа ошибок ErrorResponse
 */

public class ErrorResponse {
    private final String error;

    private String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}

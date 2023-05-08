package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * Класс описывает модель ItemRequest
 */
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private LocalDate created;
    private User requestor;
}
package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс описывает модель BookingResponseDto. Модель передается клиенту
 */

@Data
@Builder
public class BookingResponseDto {

    private Long id;

    private Item item;

    private LocalDateTime start;

    private LocalDateTime end;

    private User booker;

    private Status status;
}

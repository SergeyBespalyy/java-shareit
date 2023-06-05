package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс описывает модель BookingResponseDto. Модель передается клиенту
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private Long id;

    private Item item;

    private LocalDateTime start;

    private LocalDateTime end;

    private User booker;

    private Status status;
}

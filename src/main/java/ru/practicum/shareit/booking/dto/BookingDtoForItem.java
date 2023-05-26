package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * Класс описывает модель BookingDtoForItem. Модель передается при маппинге Item
 * (в нем требуется поле с особым названием "bookerId")
 */


@Data
@Builder
public class BookingDtoForItem {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;

    private Status status;
}

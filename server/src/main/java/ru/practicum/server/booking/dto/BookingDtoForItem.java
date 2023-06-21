package ru.practicum.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.Status;

import java.time.LocalDateTime;

/**
 * Класс описывает модель BookingDtoForItem. Модель передается при маппинге Item
 * (в нем требуется поле с особым названием "bookerId")
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoForItem {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private Status status;
}

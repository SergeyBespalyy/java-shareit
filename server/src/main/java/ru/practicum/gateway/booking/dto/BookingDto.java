package ru.practicum.gateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.booking.Status;

import java.time.LocalDateTime;

/**
 * Класс описывает модель BookingDto
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}

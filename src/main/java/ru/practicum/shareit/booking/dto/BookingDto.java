package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "itemId не может быть пустым")
    private Long itemId;

    @NotNull(message = "Поле start бронирования не может быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Дата start бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Поле end бронирования не может быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Дата end бронирования не может быть в прошлом")
    private LocalDateTime end;

    private Status status;
}

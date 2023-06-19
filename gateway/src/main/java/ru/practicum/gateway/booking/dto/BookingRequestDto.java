package ru.practicum.gateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
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
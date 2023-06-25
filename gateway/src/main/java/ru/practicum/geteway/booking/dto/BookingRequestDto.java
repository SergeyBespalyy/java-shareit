package ru.practicum.geteway.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Класс описывает модель BookingRequestDto, получаемую от клиента")
public class BookingRequestDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "itemId не может быть пустым")
    @Schema(description = "Идентификатор вещи, для бронирования", example = "1")
    private Long itemId;

    @NotNull(message = "Поле start бронирования не может быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Дата start бронирования не может быть в прошлом")
    @Schema(description = "Начало бронирования вещи", example = "2023-06-24T15:57:58.859Z")
    private LocalDateTime start;

    @NotNull(message = "Поле end бронирования не может быть пустым")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Дата end бронирования не может быть в прошлом")
    @Schema(description = "Конец бронирования вещи", example = "2023-07-24T15:57:58.859Z")
    private LocalDateTime end;

    private Status status;
}
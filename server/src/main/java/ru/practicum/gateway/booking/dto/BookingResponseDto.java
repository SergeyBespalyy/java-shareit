package ru.practicum.gateway.booking.dto;

import lombok.*;
import ru.practicum.gateway.booking.Status;

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
    private Booker booker;
    private Status status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booker {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private long id;
        private String name;
    }
}

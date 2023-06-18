package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

/**
 * Класс описывает модель ItemResponseDto. Модель передается клиенту
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Owner owner;
    private Boolean available;
    private Long requestId;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentResponseDto> comments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Owner {
        private long id;
        private String name;
    }
}

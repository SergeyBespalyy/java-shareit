package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private User owner;

    @NotNull(message = "Статус бронирования не может быть пустым")
    private Boolean available;

    private ItemRequest request;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<Comment> comments;

}

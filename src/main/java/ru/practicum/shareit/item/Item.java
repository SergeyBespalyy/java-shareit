package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 * Класс описывает модель Item
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private User owner;

    @NotBlank(message = "Статус бронирования не может быть пустым")
    private Boolean available;

    private ItemRequest request;


}

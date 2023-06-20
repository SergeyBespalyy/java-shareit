package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.user.User;

/**
 * Класс описывает модель ItemDto, получаемую от клиента
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private Long requestId;
}

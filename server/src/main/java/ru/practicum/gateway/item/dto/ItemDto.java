package ru.practicum.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.user.User;

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

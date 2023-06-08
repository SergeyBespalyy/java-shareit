package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс описывает модель ItemDtoShort. Модель передается в ItemRequestResponseDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoShort {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}

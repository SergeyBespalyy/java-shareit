package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс описывает модель ItemRequestResponseDto. Модель передается клиенту
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoShort> items = new ArrayList<>();
}

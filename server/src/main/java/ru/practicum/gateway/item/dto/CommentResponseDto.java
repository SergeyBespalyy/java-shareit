package ru.practicum.gateway.item.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Класс описывает модель CommentDto.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String text;
    private Item item;
    private String authorName;
    private LocalDateTime created;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private long id;
        private String name;
    }
}

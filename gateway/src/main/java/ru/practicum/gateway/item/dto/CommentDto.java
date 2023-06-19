package ru.practicum.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Класс описывает модель CommentDto.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    @NotBlank(message = "Отзыв не может быть пустым")
    private String text;

}

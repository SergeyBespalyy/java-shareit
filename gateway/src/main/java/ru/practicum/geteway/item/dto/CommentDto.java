package ru.practicum.geteway.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Класс описывает модель CommentDto, получаемую от клиента")
public class CommentDto {

    @NotBlank(message = "Отзыв не может быть пустым")
    @Schema(description = "Отзыв на вещь которуюю ранее бронировали", example = "Дрель серлит хорошо, рекомендую!")
    private String text;
}

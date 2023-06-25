package ru.practicum.geteway.request.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Класс описывает модель ItemRequestDto. Модель принимается от клиента")
public class ItemRequestDto {

    @Size(max = 100, message = "Описание не может быть более 100 символов ")
    @NotBlank(message = "Описание не может быть пустым")
    @Schema(description = "Описание запроса вещи, которой нет в базе данных", example = "Нужен перфоратор 200 Вт")
    private String description;
}
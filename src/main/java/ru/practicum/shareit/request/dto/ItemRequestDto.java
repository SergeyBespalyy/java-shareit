package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Класс описывает модель ItemRequestDto. Модель принимается от клиента
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
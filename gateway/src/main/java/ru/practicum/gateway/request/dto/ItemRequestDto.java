package ru.practicum.gateway.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Класс описывает модель ItemRequestDto. Модель принимается от клиента
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    @Size(max = 100, message = "Описание не может быть более 100 символов ")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
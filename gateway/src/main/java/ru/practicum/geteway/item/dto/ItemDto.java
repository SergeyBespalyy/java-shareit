package ru.practicum.geteway.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = " Класс описывает модель ItemDto, получаемую от клиента")
public class ItemDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Schema(description = " Имя вещи доступной для бронирования, ", example = "Дрель 500 Вт")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Schema(description = " Описание вещи доступной для бронирования, ", example = "Дрель почти новая, сверлит отверстия")
    private String description;

    @NotNull(message = "Статус бронирования не может быть пустым")
    @Schema(description = "Статус бронирования вещи", example = "TRUE")
    private Boolean available;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long requestId;
}

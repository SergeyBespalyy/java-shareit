package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Класс описывает модель UserDto
 */
@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;

}

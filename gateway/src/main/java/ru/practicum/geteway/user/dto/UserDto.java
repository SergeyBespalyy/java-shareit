package ru.practicum.geteway.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность пользователя")
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Schema(description = "Имя пользователя", example = "Ivan Petrov")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    @Schema(description = "Email", example = "petrov@email.com")
    private String email;

}

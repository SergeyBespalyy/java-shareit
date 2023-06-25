package ru.practicum.geteway.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.geteway.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Map;

@Tag(name = "UserController", description = "Взаимодействие с пользователями")
@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;

    @Operation(
            summary = "Добавляет пользователя в базу данных"
    )
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto user, BindingResult result) {
        log.info("Получен запрос к эндпоинту /users create");
        return userClient.create(user);
    }

    @Operation(
            summary = "Получает всех пользователей"
    )
    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получен запрос к эндпоинту: /users getAll");
        return userClient.getAll();
    }

    @Operation(
            summary = "Получает пользователя по идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Object>  getById(@PathVariable("id") Long userId) {
        log.info("Получен запрос к эндпоинту: /users geById с id={}", userId);
        return userClient.getById(userId);
    }

    @Operation(
            summary = "Обновляет пользователя по идентификатору"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long userId,
                          @RequestBody Map<Object, Object> fields,
                          BindingResult result) {
        log.info("Получен запрос к эндпоинту: /users update с id={}", userId);
        return userClient.update(userId, fields);
    }

    @Operation(
            summary = "Удаление пользователя по идентификатору"
    )
    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") @Positive Long userId) {
        log.info("Получен запрос к эндпоинту: /users delete с id={}", userId);
        userClient.delete(userId);
        return HttpStatus.OK;
    }

}

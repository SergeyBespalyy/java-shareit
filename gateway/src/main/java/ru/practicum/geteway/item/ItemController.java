package ru.practicum.geteway.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.geteway.item.dto.CommentDto;
import ru.practicum.geteway.item.dto.ItemDto;
import ru.practicum.geteway.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Map;

@Tag(name = "ItemController", description = "Взаимодействие с вещами")
@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @Operation(
            summary = "Добавляет вещь в базу данных"
    )
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.HEADER) Long userId,
                                         @RequestBody @Valid ItemDto dto,
                                         BindingResult result) {
        log.info("Получен запрос к эндпоинту /items create с headers {}", userId);
        return itemClient.create(dto, userId);
    }

    @Operation(
            summary = "Получает все вещи из базы данных"
    )
    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(Constants.HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту: /items getAll с headers {}", userId);
        return itemClient.getAll(userId);
    }

    @Operation(
            summary = "Получает данные вещи по идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(Constants.HEADER) @Positive Long userId,
                                          @PathVariable("id") @Positive Long itemId) {
        log.info("Получен запрос к эндпоинту: /items geById с id={}", itemId);
        return itemClient.getById(itemId, userId);
    }

    @Operation(
            summary = "Обновление вещи по id"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(Constants.HEADER) Long userId,
                                         @PathVariable("id") Long itemId,
                                         @RequestBody Map<Object, Object> fields,
                                         BindingResult result) {
        log.info("Получен запрос к эндпоинту: /items update с ItemId={} с headers {}", itemId, userId);
        return itemClient.update(itemId, userId, fields);
    }

    @Operation(
            summary = "Удаление вещи по id"
    )
    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") @Positive Long itemId) {
        log.info("Получен запрос к эндпоинту: /items delete с id={}", itemId);
        itemClient.delete(itemId);
        return HttpStatus.OK;
    }

    @Operation(
            summary = "Поиск вещи в базе данных",
            description = "Поиск происходит по описанию или названию вещи"
    )
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text) {
        log.info("Получен запрос к эндпоинту: items/search с text: {}", text);
        return itemClient.search(text);
    }

    @Operation(
            summary = "Добавление отзывов  на вещь после того, как взяли её в аренду",
            description = "Может взять только тот пользователь, который брал вещь в аренду"
    )
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(Constants.HEADER) Long userId,
                                             @PathVariable("itemId") @Positive Long itemId,
                                             @Valid @RequestBody CommentDto comment,
                                             BindingResult result) {
        log.info("Получен запрос к эндпоинту /items{itemId}/comment addComment с headers {}, с itemId {}", userId, itemId);
        return itemClient.createComment(itemId, userId, comment);
    }
}

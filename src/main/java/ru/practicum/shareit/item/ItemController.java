package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * Класс описывает ItemController с следующими энпоинтами
 * - GET /items/{id} -  получать данные вещи по идентификатору
 * - GET /items/ -  получать данные всех вещей
 * - POST /items/ -  добавлять вещь в память
 * - PATCH /items/{id} - обновление вещи по id
 * - DELETE  /items/{id} - удаление вещи по id
 */

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto dto,
                          BindingResult result) {
        log.info("Получен запрос к эндпоинту /items create с headers {}", userId);
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError("fieldName").getDefaultMessage();
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
        return itemService.create(dto, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту: /items getAll с headers {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту: /items geById с id={}", itemId);
        return itemService.getById(itemId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @PathVariable("id") Long itemId,
                          @RequestBody ItemDto dto,
                          BindingResult result) {
        log.info("Получен запрос к эндпоинту: /items update с ItemId={} с headers {}", itemId, userId);
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError("fieldName").getDefaultMessage();
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
        return itemService.update(itemId, dto, userId);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") Long userId) {
        log.info("Получен запрос к эндпоинту: /items delete с id={}", userId);
        itemService.delete(userId);
        return HttpStatus.OK;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        log.info("Получен запрос к эндпоинту: items/search с text: {}", text);
        return itemService.search(text);
    }
}

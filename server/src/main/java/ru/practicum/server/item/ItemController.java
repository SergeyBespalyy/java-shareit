package ru.practicum.server.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemResponseDto;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.CommentResponseDto;
import ru.practicum.server.utils.Constants;

import java.util.List;
import java.util.Map;

/**
 * Класс описывает ItemController с следующими энпоинтами
 * - GET /items/{id} -  получать данные вещи по идентификатору
 * - GET /items/ -  получать данные всех вещей
 * - POST /items/ -  добавлять вещь в память
 * - PATCH /items/{id} - обновление вещи по id
 * - DELETE  /items/{id} - удаление вещи по id
 * - POST /items/{itemId}/comment - Добавление отзывов  на вещь после того, как взяли её в аренду
 */

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(@RequestHeader(Constants.HEADER) Long userId,
                                  @RequestBody ItemDto dto,
                                  BindingResult result) {
        log.info("Получен запрос к эндпоинту /items create с headers {}", userId);
        return itemService.create(dto, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getAll(@RequestHeader(Constants.HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту: /items getAll с headers {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getById(@RequestHeader(Constants.HEADER) Long userId,
                                   @PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту: /items geById с id={}", itemId);
        return itemService.getById(itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemResponseDto update(@RequestHeader(Constants.HEADER) Long userId,
                                  @PathVariable("id") Long itemId,
                                  @RequestBody Map<Object, Object> fields,
                                  BindingResult result) {
        log.info("Получен запрос к эндпоинту: /items update с ItemId={} с headers {}", itemId, userId);
        return itemService.update(itemId, fields, userId);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту: /items delete с id={}", itemId);
        itemService.delete(itemId);
        return HttpStatus.OK;
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam("text") String text) {
        log.info("Получен запрос к эндпоинту: items/search с text: {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(Constants.HEADER) Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @RequestBody CommentDto comment,
                                         BindingResult result) {
        log.info("Получен запрос к эндпоинту /items{itemId}/comment addComment с headers {}, с itemId {}", userId, itemId);
        return itemService.createComment(comment, userId, itemId);
    }
}

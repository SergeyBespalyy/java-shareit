package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import java.util.List;

/**
 * POST /requests - добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает,
 * какая именно вещь ему нужна
 * <p>
 * GET /requests — получить список своих запросов вместе с данными об ответах на них.
 * Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
 * id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей,
 * можно будет получить подробную информацию о каждой вещи.
 * Запросы должны возвращаться в отсортированном порядке от более новых к более старым
 * <p>
 * GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
 * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
 * Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
 * Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
 * <p>
 * GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
 * что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
 */
@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto dto,
                                         BindingResult result) {
        log.info("Получен запрос к эндпоинту /requests create с headers {}", userId);
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError("fieldName").getDefaultMessage();
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
        return requestService.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getForUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту /requests getForUser с headers {}", userId);
        return requestService.getForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getOtherUsers(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос к эндпоинту /requests getOtherUsers с headers {}, from{}, size{}", userId, from, size);
        return requestService.getOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        log.info("Получен запрос к эндпоинту /requests getOtherUsers с headers {}, c requestId {}", userId, requestId);
        return requestService.getRequestById(userId, requestId);
    }
}

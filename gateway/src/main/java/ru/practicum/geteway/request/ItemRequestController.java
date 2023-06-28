package ru.practicum.geteway.request;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.geteway.request.dto.ItemRequestDto;
import ru.practicum.geteway.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Tag(name = "ItemRequestController", description = "Запрос на добавление вещи, которой нет в базе данных")
@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @Operation(
            summary = "Добавление нового запроса вещи",
            description = "Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна"
    )
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.HEADER) @Positive Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto,
                                         BindingResult result) {
        log.info("Получен запрос к эндпоинту /requests create с headers {}", userId);
        return requestClient.create(userId, itemRequestDto);
    }

    @Operation(
            summary = "Получить список своих запросов вместе с данными об ответах на них.",
            description = "Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате: " +
                    "id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.\n" +
                    " Запросы должны возвращаться в отсортированном порядке от более новых к более старыму"
    )
    @GetMapping
    public ResponseEntity<Object> getForUser(@RequestHeader(Constants.HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту /requests getForUser с headers {}", userId);
        return requestClient.getForUser(userId);
    }

    @Operation(
            summary = "Получить список запросов, созданных другими пользователями.",
            description = "С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.\n" +
                    " Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.\n" +
                    " Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения."
    )
    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsers(@RequestHeader(Constants.HEADER) Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос к эндпоинту /requests getOtherUsers с headers {}, from{}, size{}", userId, from, size);
        return requestClient.getOtherUsers(userId, from, size);
    }

    @Operation(
            summary = "Получить данные об одном конкретном запросе вместе с данными об ответах на него",
            description = "Происходит в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь."
    )
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(Constants.HEADER) Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        log.info("Получен запрос к эндпоинту /requests getOtherUsers с headers {}, c requestId {}", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}

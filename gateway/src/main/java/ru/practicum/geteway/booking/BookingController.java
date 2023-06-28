package ru.practicum.geteway.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.geteway.booking.dto.BookingRequestDto;
import ru.practicum.geteway.booking.dto.State;
import ru.practicum.geteway.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Tag(name = "BookingController", description = "Бронирование вещи")
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    @Operation(
            summary = "Получение списка всех бронирований текущего пользователя.",
            description = "Параметр state необязательный и по умолчанию равен ALL (англ. «все»). \n" +
                    "Также он может принимать значения:\n" +
                    " * CURRENT (англ. «текущие»)\n" +
                    " * PAST (англ. «завершённые»)\n" +
                    " * FUTURE (англ. «будущие»)\n" +
                    " * WAITING (англ. «ожидающие подтверждения»)\n" +
                    " * REJECTED (англ. «отклонённые»)"
    )
    public ResponseEntity<Object> getAllReservation(@RequestHeader(Constants.HEADER) long userId,
                                                    @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    @Operation(
            summary = "Добавляет запрос на бронирование вещи.",
            description = "После создания запрос находится в статусе WAITING — «ожидает подтверждения»."
    )
    public ResponseEntity<Object> addReservation(@RequestHeader(Constants.HEADER) long userId,
                                                 @RequestBody @Valid BookingRequestDto requestDto,
                                                 BindingResult result) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    @Operation(
            summary = "Получение данных о конкретном бронировании (включая его статус).\n",
            description = "Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование"
    )
    public ResponseEntity<Object> getBooking(@RequestHeader(Constants.HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }


    @PatchMapping("/{bookingId}")
    @Operation(
            summary = "Обновляет статус бронирования.",
            description = "Подтверждение или отклонение запроса на бронирование."
    )
    public ResponseEntity<Object> updateStatus(@RequestHeader(Constants.HEADER) Long userId,
                                               @PathVariable("bookingId") Long bookingId,
                                               @RequestParam("approved") Boolean approved) {
        log.info("Получен запрос к эндпоинту /bookings updateStatus с headers {}, с bookingId {}, статус {}",
                userId, bookingId, approved);
        return bookingClient.setApproved(userId, bookingId, approved);
    }


    @GetMapping("/owner")
    @Operation(
            summary = "Получение списка бронирований для всех вещей текущего пользователя.",
            description = "Получение списка бронирований для всех вещей текущего пользователя."
    )
    public ResponseEntity<Object> getReservationForOwner(@RequestHeader(Constants.HEADER) Long userId,
                                                         @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос к эндпоинту /bookings getAllReservation с state {}", stateParam);
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllReserve(userId, state, from, size);
    }
}

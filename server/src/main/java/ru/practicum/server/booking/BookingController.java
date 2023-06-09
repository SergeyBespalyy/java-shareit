package ru.practicum.server.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.utils.Constants;

import java.util.List;

/**
 * Класс описывает BookingController с следующими энпоинтами
 * - POST /bookings/ -  добавляет запрос на бронирование вещи. После создания запрос находится в статусе WAITING — «ожидает подтверждения».
 * - PATCH /bookings/{bookingId} - обновляет статус бронирования. Подтверждение или отклонение запроса на бронирование.
 * - GET /bookings/{bookingId} -  Получение данных о конкретном бронировании (включая его статус).
 * Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование
 * - GET /bookings?state={state} Получение списка всех бронирований текущего пользователя.
 * Параметр state необязательный и по умолчанию равен ALL (англ. «все»). Также он может принимать значения
 * CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»), FUTURE (англ. «будущие»),
 * WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»)
 * - GET /bookings/owner?state={state} - Получение списка бронирований для всех вещей текущего пользователя.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addReservation(@RequestHeader(Constants.HEADER) Long userId,
                                             @RequestBody BookingDto dto,
                                             BindingResult result) {
        log.info("Получен запрос к эндпоинту /bookings addReservation с headers {}", userId);
        return bookingService.create(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateStatus(@RequestHeader(Constants.HEADER) Long userId,
                                           @PathVariable("bookingId") Long bookingId,
                                           @RequestParam("approved") Boolean approved) {
        log.info("Получен запрос к эндпоинту /bookings updateStatus с headers {}, с bookingId {}, статус {}",
                userId, bookingId, approved);
        return bookingService.setApproved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader(Constants.HEADER) Long userId,
                                      @PathVariable("bookingId") Long bookingId) {
        log.info("Получен запрос к эндпоинту /bookings getById с headers {}, с bookingId {}", userId, bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllReservation(@RequestHeader(Constants.HEADER) Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") State state,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) throws Throwable {
        log.info("Получен запрос к эндпоинту /bookings getAllReservation с state {}", state);
        return bookingService.getAllReserve(userId, state, "booker", from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getReservationForOwner(@RequestHeader(Constants.HEADER) Long userId,
                                                           @RequestParam(value = "state", defaultValue = "ALL") State state,
                                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) throws Throwable {
        log.info("Получен запрос к эндпоинту /bookings getAllReservation с state {}", state);
        return bookingService.getAllReserve(userId, state, "owner", from, size);
    }
}
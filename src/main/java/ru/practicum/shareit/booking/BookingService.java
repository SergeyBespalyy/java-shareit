package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemIsNotAvailableForBookingException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс описывает BookingService, с основной логикой
 */


@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingResponseDto create(BookingDto dto, Long bookerId) {
        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().equals(dto.getEnd())) {
            throw new ItemIsNotAvailableForBookingException("Дата начала позже или равна окончанию бронирования");
        }
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new ValidationIdException("Item не найден"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (!itemDto.getAvailable()) {
            throw new ItemIsNotAvailableForBookingException("Вещь не доступна для бронирования");
        }

        if (itemDto.getOwner().getId().equals(bookerId)) {
            throw new ValidationIdException("Пользователь не может забронировать свою вещь");
        }
        Item newItem = ItemMapper.toItem(itemDto, itemDto.getOwner());
        User booker = UserMapper.toUser(userService.getById(bookerId));
        Booking booking = BookingMapper.toBooking(dto, newItem, booker);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findBookingOwner(bookingId, userId);

        if (booking == null) {
            throw new ValidationIdException("Booking не найден");
        }

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ItemIsNotAvailableForBookingException("Cтатус APPROVED уже установлен");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findBookingOwnerOrBooker(bookingId, userId);
        if (booking == null) {
            throw new ValidationIdException("Booking не найден");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingResponseDto> getAllReserve(Long userId, State state) {
        if (state == null) {
            state = State.ALL;
        }

        List<Booking> list = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                list = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case FUTURE:
                list = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time);
                break;
            case WAITING:
                list = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case CURRENT:
                list = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
                break;
            case PAST:
                list = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time);
                break;
            case REJECTED:
                list = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("UNSUPPORTED_STATUS");
        }
        if (list.isEmpty()) {
            throw new ValidationIdException("Бронирование не найдено");
        }

        return list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public List<BookingResponseDto> getAllReserveForOwner(Long userId, State state) {
        if (state == null) {
            state = State.ALL;
        }
        List<Booking> list = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                list = bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
                break;
            case FUTURE:
                list = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, time);
                break;
            case WAITING:
                list = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case CURRENT:
                list = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
                break;
            case PAST:
                list = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, time);
                break;
            case REJECTED:
                list = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("UNSUPPORTED_STATUS");
        }
        if (list.isEmpty()) {
            throw new ValidationIdException("Бронирование не найдено");
        }

        return list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

    }


}

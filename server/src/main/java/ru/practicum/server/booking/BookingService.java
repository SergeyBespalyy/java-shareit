package ru.practicum.server.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.exceptions.ItemIsNotAvailableForBookingException;
import ru.practicum.server.exceptions.ValidationIdException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;
import ru.practicum.server.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс описывает BookingService, с основной логикой
 */

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
@Validated
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

        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableForBookingException("Вещь не доступна для бронирования");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationIdException("Пользователь не может забронировать свою вещь");
        }

        User booker = UserMapper.toUser(userService.getById(bookerId));
        Booking booking = BookingMapper.toBooking(dto, item, booker);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto setApproved(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findBookingOwner(bookingId, userId);

        if (booking == null) {
            throw new ValidationIdException("Booking не найден");
        }

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ItemIsNotAvailableForBookingException("Статус APPROVED уже установлен");
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

    public List<BookingResponseDto> getAllReserve(Long userId, State state, String typeUser, int from, int size) throws Throwable {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> list;
        LocalDateTime time = LocalDateTime.now();
        boolean isOwner = typeUser.equals("owner");

        switch (state) {
            case ALL:
                if (isOwner) {
                    list = bookingRepository.findAllByOwnerIdOrderByStartDesc(userId, page);
                } else {
                    list = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                }
                break;
            case FUTURE:
                if (isOwner) {
                    list = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, time, page);
                } else {
                    list = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, time, page);
                }
                break;
            case WAITING:
                if (isOwner) {
                    list = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                } else {
                    list = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
                }
                break;
            case CURRENT:
                if (isOwner) {
                    list = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time, page);
                } else {
                    list = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time, page);
                }
                break;
            case PAST:
                if (isOwner) {
                    list = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, time, page);
                } else {
                    list = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, time, page);
                }
                break;
            case REJECTED:
                if (isOwner) {
                    list = bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                } else {
                    list = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
                }
                break;
            default:
                throw new Throwable("UNSUPPORTED_STATUS");
        }

        if (list.isEmpty()) {
            throw new ValidationIdException("Бронирование не найдено");
        }

        return list.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}

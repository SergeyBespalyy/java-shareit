package ru.practicum.gateway.item;

import lombok.experimental.UtilityClass;
import ru.practicum.gateway.booking.Booking;
import ru.practicum.gateway.booking.BookingMapper;
import ru.practicum.gateway.booking.Status;
import ru.practicum.gateway.booking.dto.BookingDtoForItem;
import ru.practicum.gateway.item.dto.CommentResponseDto;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.item.dto.ItemDtoShort;
import ru.practicum.gateway.item.dto.ItemResponseDto;
import ru.practicum.gateway.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Класс описывает ItemMapper, переводит итем в ДТО и обратно
 */

@UtilityClass
public class ItemMapper {
    public ItemDtoShort toItemDtoShort(Item item) {
        return ItemDtoShort
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public Item toItem(ItemDto dto, User user) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(user)
                .requestId(dto.getRequestId())
                .build();
    }

    public ItemResponseDto toItemResponseDto(Item item, List<Booking> booking, List<CommentResponseDto> comment) {
        BookingDtoForItem bookingLast = null;
        BookingDtoForItem bookingNext = null;
        LocalDateTime time = LocalDateTime.now();

        if (!booking.isEmpty()) {

            Optional<Booking> bookingLastOld = booking.stream()
                    .filter(b -> (b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED)))
                    .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                    .sorted(Comparator.comparing(Booking::getId).reversed())
                    .findFirst();

            Optional<Booking> bookingNextOld = booking.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(b -> b.getStart().isAfter(time))
                    .findFirst();
            if (bookingLastOld.isPresent()) {
                bookingLast = BookingMapper.toBookingDtoForItem(bookingLastOld.get());
            }
            if (bookingNextOld.isPresent()) {
                bookingNext = BookingMapper.toBookingDtoForItem(bookingNextOld.get());
            }

        }
        return ItemResponseDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(new ItemResponseDto.Owner(item.getOwner().getId(), item.getOwner().getName()))
                .available(item.getAvailable())
                .lastBooking(bookingLast)
                .nextBooking(bookingNext)
                .comments(comment)
                .requestId(item.getRequestId())
                .build();
    }
}


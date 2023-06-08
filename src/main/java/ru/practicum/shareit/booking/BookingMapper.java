package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class BookingMapper {

    public BookingResponseDto toBookingDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .item(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()))
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(BookingDto dto, Item item, User booker) {
        return Booking.builder()
                .id(dto.getId())
                .item(item)
                .start(dto.getStart())
                .end(dto.getEnd())
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    public BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return BookingDtoForItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

}

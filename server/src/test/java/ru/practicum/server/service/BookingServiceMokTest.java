package ru.practicum.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.booking.*;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.exceptions.ValidationIdException;
import ru.practicum.server.item.Item;
import ru.practicum.server.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceMokTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;
    private List<Booking> bookings;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");

        owner = new User(2L, "newUser", "newUser@user.com");

        item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(000);
        LocalDateTime end = start.plusDays(1).withNano(000);

        booking = new Booking(1L, item, start, end, booker, null);

        bookingResponseDto = BookingResponseDto
                .builder()
                .id(booking.getId())
                .status(Status.WAITING)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingResponseDto.Item(item.getId(), item.getName()))
                .booker(new BookingResponseDto.Booker(booker.getId(), booker.getName()))
                .build();

    }


    @Test
    void getAllBookingsStateEmpty() {
        Long userId = 1L;
        State state = State.PAST;
        String typeUser = "owner";
        int from = 0;
        int size = 10;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);

        assertThrows(ValidationIdException.class, () -> {
            bookingService.getAllReserve(userId, state, typeUser, from, size);
        });
    }

    @Test
    void getAllBookingsStateFutureOwner() throws Throwable {
        Long userId = 1L;
        State state = State.FUTURE;
        String typeUser = "owner";
        int from = 0;
        int size = 10;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(booking);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);

        List<BookingResponseDto> bookingResponseDto = bookingService.getAllReserve(userId, state, typeUser, from, size);
        assertNotNull(bookingResponseDto);
        assertEquals(2, bookingResponseDto.size());
    }

    @Test
    void getAllBookingsStateWAITING() throws Throwable {
        Long userId = 1L;
        State state = State.WAITING;
        String typeUser = "booker";
        int from = 0;
        int size = 10;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(booking);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);

        List<BookingResponseDto> bookingResponseDto = bookingService.getAllReserve(userId, state, typeUser, from, size);
        assertNotNull(bookingResponseDto);
        assertEquals(2, bookingResponseDto.size());
    }

    @Test
    void getAllBookingsStateCURRENT() throws Throwable {
        Long userId = 1L;
        State state = State.CURRENT;
        String typeUser = "booker";
        int from = 0;
        int size = 10;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(booking);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(bookingList);

        List<BookingResponseDto> bookingResponseDto = bookingService.getAllReserve(userId, state, typeUser, from, size);
        assertNotNull(bookingResponseDto);
        assertEquals(2, bookingResponseDto.size());
    }

    @Test
    void getAllBookingsStateREJECTED() throws Throwable {
        Long userId = 1L;
        State state = State.REJECTED;
        String typeUser = "booker";
        int from = 0;
        int size = 10;

        User user = new User();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(booking);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);

        List<BookingResponseDto> bookingResponseDto = bookingService.getAllReserve(userId, state, typeUser, from, size);
        assertNotNull(bookingResponseDto);
        assertEquals(2, bookingResponseDto.size());
    }
}
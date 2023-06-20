package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.*;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.exceptions.ItemIsNotAvailableForBookingException;
import ru.practicum.server.exceptions.ValidationIdException;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingService bookingService;

    @Test
    public void testCreateBooking() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setItemId(item.getId());

        BookingResponseDto bookingResponseDto = bookingService.create(bookingDto, booker.getId());

        assertNotNull(bookingResponseDto.getId());
        Assertions.assertEquals(bookingDto.getStart(), bookingResponseDto.getStart());
        Assertions.assertEquals(bookingDto.getEnd(), bookingResponseDto.getEnd());
        Assertions.assertEquals(item.getId(), bookingResponseDto.getItem().getId());
        Assertions.assertEquals(booker.getId(), bookingResponseDto.getBooker().getId());
    }

    @Test
    public void testCreateBookingForOwner() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setItemId(item.getId());

        assertThrows(ValidationIdException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });
    }

    @Test
    public void testCreateBookingAvailableFalse() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(false);
        item.setOwner(user);
        itemRepository.save(item);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setItemId(item.getId());

        assertThrows(ItemIsNotAvailableForBookingException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });

    }

    @Test
    public void testSetApproved() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = bookingService.setApproved(owner.getId(), booking.getId(), true);

        Assertions.assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    public void testSetREJECTED() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = bookingService.setApproved(owner.getId(), booking.getId(), false);

        Assertions.assertEquals(Status.REJECTED, bookingResponseDto.getStatus());
    }

    @Test
    public void testSetApprovedTwo() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = bookingService.setApproved(owner.getId(), booking.getId(), true);

        Assertions.assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
        assertThrows(ItemIsNotAvailableForBookingException.class, () -> {
            bookingService.setApproved(owner.getId(), booking.getId(), true);
        });
    }

    @Test
    public void testSetApprovedDataFalse() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = bookingService.setApproved(owner.getId(), booking.getId(), true);

        Assertions.assertEquals(Status.APPROVED, bookingResponseDto.getStatus());
        assertThrows(ItemIsNotAvailableForBookingException.class, () -> {
            bookingService.setApproved(owner.getId(), booking.getId(), true);
        });
    }

    @Test
    public void testGetById() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = bookingService.getById(owner.getId(), booking.getId());

        assertNotNull(bookingResponseDto);
        Assertions.assertEquals(booking.getId(), bookingResponseDto.getId());
    }

    @Test
    public void testGetByIdNotFound() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        assertThrows(ValidationIdException.class, () -> {
            bookingService.getById(owner.getId(), 5L);
        });
    }

    @Test
    public void testGetAllReserveForOwner() throws Throwable {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        List<BookingResponseDto> bookingResponseDtoList = bookingService.getAllReserve(owner.getId(), State.ALL, "owner", 0, 10);

        assertNotNull(bookingResponseDtoList);
        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    public void testGetAllReserve() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);


        assertThrows(ValidationIdException.class, () -> {
            bookingService.getAllReserve(owner.getId(), State.ALL, "booker", 0, 10);
        });
    }

    @Test
    public void testGetAllReserveForOwnerFuture() throws Throwable {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);

        List<BookingResponseDto> bookingResponseDtoList = bookingService.getAllReserve(owner.getId(), State.ALL, "owner", 0, 10);

        assertNotNull(bookingResponseDtoList);
        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    public void testGetAllReserveFuture() {

        User owner = new User();
        owner.setEmail("test@test.com");
        owner.setName("Test User");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("Booker@Booker.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        bookingRepository.save(booking);


        assertThrows(ValidationIdException.class, () -> {
            bookingService.getAllReserve(owner.getId(), State.ALL, "booker", 0, 10);
        });
    }
}
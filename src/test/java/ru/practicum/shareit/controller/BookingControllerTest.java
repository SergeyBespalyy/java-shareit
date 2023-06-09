package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.ItemIsNotAvailableForBookingException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private User booker;
    private User owner;

    private Item item;
    private BookingDto bookingDto;
    private BookingResponseDto bookingResponseDto;

    LocalDateTime start;
    LocalDateTime end;
    @MockBean
    private BookingService bookingService;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");

        owner = new User(2L, "newUser", "newUser@user.com");

        item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);

        start = LocalDateTime.now().plusMinutes(1).withNano(000);
        end = start.plusDays(1).withNano(000);

        bookingDto = new BookingDto(1L, 1L, start, end, null);

        bookingResponseDto = BookingResponseDto
                .builder()
                .id(bookingDto.getId())
                .status(Status.WAITING)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(new BookingResponseDto.Item(item.getId(), item.getName()))
                .booker(new BookingResponseDto.Booker(booker.getId(), booker.getName()))
                .build();

    }

    @Test
    public void shouldCreateBooking() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingResponseDto);

        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    public void shouldGetBookingsById() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("user"));
    }

    @Test
    public void shouldBookingsUpdateUser() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;
        bookingResponseDto.setStatus(Status.APPROVED);

        when(bookingService.setApproved(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void shouldBookingsUpdateUserWithoutBooking() throws Exception {
        Integer bookingId = 99;
        Integer userId = 1;

        when(bookingService.setApproved(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ValidationIdException("Booking не найден"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("ValidationIdException")));
    }

    @Test
    public void shouldBookingsUpdateUserWithApproved() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        when(bookingService.setApproved(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ItemIsNotAvailableForBookingException("Статус APPROVED уже установлен"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("ItemIsNotAvailableForBookingException")));

    }

    @Test
    public void shouldBookingsUpdateWithApprovedFalse() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;
        bookingResponseDto.setStatus(Status.REJECTED);

        when(bookingService.setApproved(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=false", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

    }

    @Test
    public void shouldBookingsUpdateWithUnknownUser() throws Exception {
        Integer bookingId = 1;
        Integer userId = 100;

        when(bookingService.setApproved(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ValidationIdException("Booking не найден"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("ValidationIdException")));
    }

    @Test
    public void shouldBookingsAllReservation() throws Exception {
        Integer userId = 2;

        when(bookingService.getAllReserve(anyLong(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto, bookingResponseDto, bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void shouldBookingsAllReservationOwner() throws Exception {
        Integer userId = 2;

        when(bookingService.getAllReserve(anyLong(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto, bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldBookingsWithoutSizeMinus() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?size=-1&from=0", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsWithoutFromMinus() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?size=10&from=-1", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

//    @Test
//    public void shouldBookingsAllReservationAll() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 2;
//
//        mockMvc.perform(get("/bookings", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//
//    @Test
//    public void shouldBookingsAllReservationFuture() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 2;
//
//        mockMvc.perform(get("/bookings?state=FUTURE", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//
//    @Test
//    public void shouldBookingsAllReservationWAITING() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 2;
//
//        mockMvc.perform(get("/bookings?state=WAITING", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//
//    @Test
//    public void shouldBookingsAllReservationREJECTED() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 2;
//
//        mockMvc.perform(get("/bookings?state=REJECTED", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//
//    @Test
//    public void shouldBookingsAllReservationForOwnerAll() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//
//    @Test
//    public void shouldBookingsAllReservationForOwnerFUTURE() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?state=FUTURE", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//
//    @Test
//    public void shouldBookingsAllReservationForOwnerWaiting() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?state=WAITING", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)));
//    }
//
//    @Test
//    public void shouldBookingsAllReservationForOwnerREJECTED() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?state=REJECTED", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsAllReservationPAST() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 2;
//
//        mockMvc.perform(get("/bookings?state=PAST", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsAllReservationCURRENT() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 2;
//
//        mockMvc.perform(get("/bookings?state=CURRENT", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsAllReservationForOwnerPAST() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?state=PAST", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsAllReservationForOwnerCURRENT() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?state=CURRENT", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsAllReservationUnsupportedStatus() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?state=PASTPast", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingCreateWitchFalseData() throws Exception {
//
//        Long bookerId = 2L;
//        start = LocalDateTime.now().plusHours(1);
//        end = start.minusMinutes(30);
//
//        bookingDto = new BookingDto(1L, 1L, start, end, null);
//        String jsonBooking = objectMapper.writeValueAsString(bookingDto);
//
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", bookerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBooking))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingCreateWitchEmptyData() throws Exception {
//
//        Long bookerId = 2L;
//
//        bookingDto = new BookingDto(1L, 1L, null, null, null);
//        String jsonBooking = objectMapper.writeValueAsString(bookingDto);
//
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", bookerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBooking))
//                .andExpect(status().is4xxClientError());
//    }
//
//
//    @Test
//    public void shouldBookingCreateWitchFalseAvialable() throws Exception {
//
//        Long bookerId = 2L;
//        start = LocalDateTime.now().plusMinutes(1);
//        end = start.plusDays(1);
//
//        Item item = new Item(2L, "Дрель++", "Простая дрель++", owner, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//        Long userId = 1L;
//        mockMvc.perform(post("/items")
//                .header("X-Sharer-User-Id", userId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonItem));
//
//        bookingDto = new BookingDto(1L, 2L, start, end, null);
//        String jsonBooking = objectMapper.writeValueAsString(bookingDto);
//
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", bookerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBooking))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingCreateWithFalseUserEqualsOwner() throws Exception {
//
//        Long bookerId = 1L;
//        start = LocalDateTime.now().plusMinutes(1);
//        end = start.plusDays(1);
//
//        bookingDto = new BookingDto(1L, 1L, start, end, null);
//        String jsonBooking = objectMapper.writeValueAsString(bookingDto);
//
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", bookerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBooking))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsWithoutSizeMinus() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?size=-1&from=0", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldBookingsWithoutFromMinus() throws Exception {
//        Integer bookingId = 1;
//        Integer userId = 1;
//
//        mockMvc.perform(get("/bookings/owner?size=10&from=-1", bookingId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//    }
}

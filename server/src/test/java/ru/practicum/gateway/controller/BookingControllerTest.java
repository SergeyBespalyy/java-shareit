package ru.practicum.gateway.controller;

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
import ru.practicum.gateway.booking.BookingController;
import ru.practicum.gateway.booking.BookingService;
import ru.practicum.gateway.booking.Status;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingResponseDto;
import ru.practicum.gateway.exceptions.ItemIsNotAvailableForBookingException;
import ru.practicum.gateway.exceptions.ValidationIdException;
import ru.practicum.gateway.item.Item;
import ru.practicum.gateway.user.User;

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
    public void shouldBookingsAllReservation() throws Throwable {
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
    public void shouldBookingsAllReservationOwner() throws Throwable {
        Integer userId = 2;

        when(bookingService.getAllReserve(anyLong(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto, bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

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

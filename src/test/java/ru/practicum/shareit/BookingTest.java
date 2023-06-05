package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private User booker;
    private User owner;

    private BookingDto bookingDto;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");
        String jsonBooker = objectMapper.writeValueAsString(booker);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBooker));

        owner = new User(2L, "newUser", "newUser@user.com");
        String jsonOwner = objectMapper.writeValueAsString(owner);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonOwner));

        Item item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        Long userId = 1L;
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem));


        Long bookerId = 2L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        bookingDto = new BookingDto(1L, 1L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", bookerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBooking));
    }

    @Test
    public void shouldGetBookingsById() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void shouldBookingsUpdateUser() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void shouldBookingsUpdateUserWithoutBooking() throws Exception {
        Integer bookingId = 99;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldBookingsUpdateUserWithApproved() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldBookingsUpdateWithApprovedFalse() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=false", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

    }

    @Test
    public void shouldBookingsUpdateWithUnckownUser() throws Exception {
        Integer bookingId = 1;
        Integer userId = 100;


        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsUpdateWithUnckownBooking() throws Exception {
        Integer bookingId = 100;
        Integer userId = 1;


        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsAllReservationAll() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldBookingsAllReservationFuture() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=FUTURE", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldBookingsAllReservationWAITING() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=WAITING", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldBookingsAllReservationREJECTED() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=REJECTED", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldBookingsAllReservationForOwnerAll() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldBookingsAllReservationForOwnerFUTURE() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=FUTURE", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldBookingsAllReservationForOwnerWaiting() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=WAITING", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldBookingsAllReservationForOwnerREJECTED() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=REJECTED", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsAllReservationPAST() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=PAST", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsAllReservationCURRENT() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        mockMvc.perform(get("/bookings?state=CURRENT", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsAllReservationForOwnerPAST() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=PAST", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsAllReservationForOwnerCURRENT() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=CURRENT", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsAllReservationUnsupportedStatus() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?state=PASTPast", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingCreateWitchFalseData() throws Exception {

        Long bookerId = 2L;
        start = LocalDateTime.now().plusHours(1);
        end = start.minusMinutes(30);

        bookingDto = new BookingDto(1L, 1L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingCreateWitchEmptyData() throws Exception {

        Long bookerId = 2L;

        bookingDto = new BookingDto(1L, 1L, null, null, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldBookingCreateWitchFalseAvialable() throws Exception {

        Long bookerId = 2L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        Item item = new Item(2L, "Дрель++", "Простая дрель++", owner, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        Long userId = 1L;
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem));

        bookingDto = new BookingDto(1L, 2L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingCreateWithFalseUserEqualsOwner() throws Exception {

        Long bookerId = 1L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        bookingDto = new BookingDto(1L, 1L, start, end, null);
        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().is4xxClientError());
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
}

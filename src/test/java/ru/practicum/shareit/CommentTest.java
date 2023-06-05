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
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final ItemService itemService;
    private final CommentRepository commentRepository;

    private User booker;
    private User owner;

    private BookingDto bookingDto;
    private CommentDto commentDto;
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

        commentDto = new CommentDto(null, "Add comment from user2", null, null, null);
        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        Long itemId = 1L;
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", bookerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommentDto));
    }

    @Test
    public void shouldGetComment() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/{itemId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner.id").value(1));

        Optional<Comment> comment = commentRepository.findById(1L);

        if (comment.isPresent()) {
            assertEquals(1, comment.get().getId());
            assertEquals("Add comment from user2", comment.get().getText());
            assertEquals("newUser", comment.get().getAuthorName());
        }


    }

}

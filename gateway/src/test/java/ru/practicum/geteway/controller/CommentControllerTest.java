package ru.practicum.geteway.controller;

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
import ru.practicum.geteway.item.ItemClient;
import ru.practicum.geteway.item.ItemController;
import ru.practicum.geteway.item.dto.CommentDto;
import ru.practicum.geteway.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemDto itemDto;
    private CommentDto commentDto;
    LocalDateTime start;
    LocalDateTime end;
    Long bookerId = 1L;
    Long itemId = 1L;

    @BeforeEach
    public void setUp() throws Exception {

        itemDto = new ItemDto(1L, "Дрель", "Простая дрель", true, null);

        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);

        commentDto = new CommentDto("Add comment from user2");

    }

    @Test
    public void shouldCreateComment() throws Exception {

        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentDto))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateCommentWithEmptyText() throws Exception {
        commentDto.setText("");

        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentDto))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldCreateCommentWithNegativeItem() throws Exception {

        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", -1)
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentDto))
                .andExpect(status().is4xxClientError());
    }
}

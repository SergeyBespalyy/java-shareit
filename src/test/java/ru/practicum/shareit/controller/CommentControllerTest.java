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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;


    private User booker;
    private User owner;
    private Item item;
    private CommentDto commentDto;
    private CommentResponseDto commentResponseDto;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");

        owner = new User(2L, "newUser", "newUser@user.com");

        item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);

        Long bookerId = 2L;
        start = LocalDateTime.now().plusMinutes(1);
        end = start.plusDays(1);


        commentDto = new CommentDto(null, "Add comment from user2", item, booker.getName(), end);

        commentResponseDto = CommentResponseDto
                .builder()
                .id(1L)
                .authorName(commentDto.getAuthorName())
                .created(commentDto.getCreated())
                .item(new CommentResponseDto.Item(item.getId(), item.getName()))
                .text(commentDto.getText())
                .build();
    }

    @Test
    public void shouldCreateComment() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentResponseDto);

        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.authorName").value("user"))
                .andExpect(jsonPath("$.text").value("Add comment from user2"));
    }

    @Test
    public void shouldCreateCommentWithEmptyText() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentResponseDto);

        commentDto.setText("");

        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentDto))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldCreateCommentWithNegativeItem() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentResponseDto);

        String jsonCommentDto = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", -1)
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentDto))
                .andExpect(status().is4xxClientError());
    }
}

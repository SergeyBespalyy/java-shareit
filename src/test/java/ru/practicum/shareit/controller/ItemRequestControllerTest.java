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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private User user;
    private User userTwo;
    private ItemRequestDto item;
    private ItemRequestResponseDto itemResponse;
    @MockBean
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void setUp() throws Exception {
        user = new User(1L, "user", "user@user.com");

        userTwo = new User(2L, "userNew", "userNew@userNew.com");

        item = new ItemRequestDto("Хотел бы воспользоваться щёткой для обуви");

        itemResponse = ItemRequestResponseDto.builder()
                .id(1L)
                .description(item.getDescription())
                .items(new ArrayList<>())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void shouldItemRequestWithoutUser() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = -99L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemRequestWithEmptyDescription() throws Exception {
        ItemRequestDto item = new ItemRequestDto(null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 1L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldGetItemRequestWithoutUser() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldGetItemRequestWithoutRequest() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getForUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetItemRequestWithoutPaginationParams() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getForUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetItemRequestWithFrom0Size0() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=0&size=0")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithFromMinSize20() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=-1&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithFrom0SizeMin() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=0&size=-1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithFrom0Size20() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getOtherUsers(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldItemRequestAdd() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemResponse);
        Long userId = 1L;

        when(itemRequestService.create(any(), anyLong())).thenReturn(itemResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void shouldItemRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemResponse);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}

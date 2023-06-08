package ru.practicum.shareit.Controller;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private User booker;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    @MockBean
    private ItemService itemService;

    @BeforeEach
    public void setUp() throws Exception {
        booker = new User(1L, "user", "user@user.com");

        owner = new User(2L, "newUser", "newUser@user.com");

        item = new Item(1L, "Дрель", "Простая дрель", owner, true, null);

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name(item.getName())
                .description(item.getDescription())
                .owner(new ItemResponseDto.Owner(owner.getId(), owner.getName()))
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name(item.getName())
                .description(item.getDescription())
                .owner(owner)
                .available(true)
                .build();

    }

    @Test
    public void shouldCreateBooking() throws Exception {
        when(itemService.create(any(), anyLong())).thenReturn(itemResponseDto);

        String json = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.owner.id").value(2))
                .andExpect(jsonPath("$.owner.name").value("newUser"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void shouldGetItemById() throws Exception {
        Integer itemId = 1;
        Integer userId = 1;

        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void shouldItemWithoutXSharerUserId() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", owner, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemWithoutAvailable() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", owner, null, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemWitEmptyName() throws Exception {
        Item item = new Item(2L, "", "Простая дрель", owner, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldItemWitEmptyDescription() throws Exception {
        Item item = new Item(2L, "Дрель", "", owner, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemAll() throws Exception {
        Integer userId = 1;

        when(itemService.getAll(anyLong())).thenReturn(List.of(itemResponseDto, itemResponseDto, itemResponseDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value("true"));
    }

    @Test
    public void shouldItemUpdate() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        String json = objectMapper.writeValueAsString(itemDto);

        when(itemService.update(anyLong(), anyMap(), anyLong())).thenReturn(itemResponseDto);

        itemResponseDto.setName("Дрель+");

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель+"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void shouldDeleteItem() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSearh() throws Exception {
        Integer userId = 1;

        when(itemService.search(anyString())).thenReturn(List.of(itemResponseDto, itemResponseDto));

        mockMvc.perform(get("/items/search?text=дрель")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value("true"));
    }

////
//    @Test
//    public void shouldItemWithNotFoundUser() throws Exception {
//        Item item = new Item(2L, "Дрель", "Простая дрель", user, true, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//        Long userId = 99L;
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().is4xxClientError());
//    }
//


//
//
//
//    @Test
//    public void shouldItemUpdateWithoutXSharerUserId() throws Exception {
//        Item item = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//        Long userId = 1L;
//
//        mockMvc.perform(patch("/items/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldItemUpdateWithUnckownUser() throws Exception {
//        Item item = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//        Long userId = 99L;
//
//        mockMvc.perform(patch("/items/1")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    public void shouldItemUpdateAvailable() throws Exception {
//        Long itemId = 1L;
//        Long userId = 1L;
//
//        Map<Object, Object> fields = new HashMap<>();
//        fields.put("available", false);
//
//        itemService.update(itemId, fields, userId);
//
//        mockMvc.perform(get("/items/{id}", itemId)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("Дрель"))
//                .andExpect(jsonPath("$.description").value("Простая дрель"))
//                .andExpect(jsonPath("$.available").value("false"));
//    }
//
//    @Test
//    public void shouldItemUpdateDescription() throws Exception {
//        String jsonItem = "{\"description\":\"Аккумуляторная дрель + аккумулятор\"}";
//        Long userId = 1L;
//
//        mockMvc.perform(patch("/items/1")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(jsonPath("$.name").value("Дрель"))
//                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель + аккумулятор"))
//                .andExpect(jsonPath("$.available").value("true"));
//    }
//
//    @Test
//    public void shouldItemUpdateName() throws Exception {
//        String jsonItem = "{\"name\":\"Аккумуляторная дрель\"}";
//        Long userId = 1L;
//
//        mockMvc.perform(patch("/items/1")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(jsonPath("$.name").value("Аккумуляторная дрель"))
//                .andExpect(jsonPath("$.description").value("Простая дрель"))
//                .andExpect(jsonPath("$.available").value("true"));
//    }
//
//    @Test
//    public void shouldGetItemByHeader() throws Exception {
//        Long userId = 1L;
//        mockMvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("Дрель"))
//                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
//                .andExpect(jsonPath("$[0].available").value("true"));
//
//        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//        User userNew = new User(2L, "userNew", "userNew@userNew.com");
//        String jsonUser = objectMapper.writeValueAsString(userNew);
//
//
//        mockMvc.perform(post("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonUser));
//
//        Long userIdNew = 2L;
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userIdNew)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", userIdNew))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(2))
//                .andExpect(jsonPath("$[0].name").value("Дрель+"))
//                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
//                .andExpect(jsonPath("$[0].available").value("false"));
//
//    }
//
//    @Test
//    public void shouldGetItemByHeaderWhenTwoItem() throws Exception {
//        Long userId = 1L;
//        mockMvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("Дрель"))
//                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
//                .andExpect(jsonPath("$[0].available").value("true"));
//
//        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].name").value("Дрель+"))
//                .andExpect(jsonPath("$[1].description").value("Аккумуляторная дрель"))
//                .andExpect(jsonPath("$[1].available").value("false"));
//    }
//
//    @Test
//    public void shouldSearchItemByText() throws Exception {
//        Long userId = 1L;
//        String text = "аккУМУляторная";
//
//        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().isOk());
//
//
//        mockMvc.perform(get("/items/search?text=" + text)
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(2))
//                .andExpect(jsonPath("$[0].name").value("Дрель+"))
//                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
//                .andExpect(jsonPath("$[0].available").value("true"));
//
//    }
//
//    @Test
//    public void shouldSearchItemByTextForTwoItem() throws Exception {
//        Long userId = 1L;
//        String text = "дРелЬ";
//
//        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().isOk());
//
//
//        mockMvc.perform(get("/items/search?text=" + text)
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("Дрель"))
//                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
//                .andExpect(jsonPath("$[0].available").value("true"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].name").value("Дрель+"))
//                .andExpect(jsonPath("$[1].description").value("Аккумуляторная дрель"))
//                .andExpect(jsonPath("$[1].available").value("true"));
//
//    }
//
//    @Test
//    public void shouldSearchItemByTextWhenEmpty() throws Exception {
//        Long userId = 1L;
//        String text = "";
//
//        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().isOk());
//
//
//        mockMvc.perform(get("/items/search?text=" + text)
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(0)));
//
//    }
//
//    @Test
//    public void shouldDeleteItem() throws Exception {
//        Long userId = 1L;
//
//        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(delete("/items/{id}", 2L)
//                        .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/items/{id}", 2L)
//                        .header("X-Sharer-User-Id", userId))
//                .andDo(print())
//                .andExpect(status().is4xxClientError());
//
//    }
//
//    @Test
//    public void shouldCreateItemWithEmptyName() throws Exception {
//        Long userId = 1L;
//
//        Item item = new Item(2L, null, "Аккумуляторная дрель", user, false, null);
//        String jsonItem = objectMapper.writeValueAsString(item);
//
//        mockMvc.perform(post("/items")
//                        .header("X-Sharer-User-Id", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonItem))
//                .andExpect(status().is4xxClientError());
//    }
}

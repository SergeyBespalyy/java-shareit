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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;
    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        user = new User(1L, "user", "user@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        Item item = new Item(1L, "Дрель", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        Long userId = 1L;
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem));
    }

    @Test
    public void shouldGetItemById() throws Exception {
        Integer itemId = 1;
        mockMvc.perform(get("/items/{id}", itemId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void shouldItemWithoutXSharerUserId() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemWithNotFoundUser() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 99L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemWithoutAvailable() throws Exception {
        Item item = new Item(2L, "Дрель", "Простая дрель", user, null, null);
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
        Item item = new Item(2L, "", "Простая дрель", user, true, null);
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
        Item item = new Item(2L, "Дрель", "", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemUpdate() throws Exception {
        Item item = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель+"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value("false"));
    }

    @Test
    public void shouldItemUpdateWithoutXSharerUserId() throws Exception {
        Item item = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemUpdateWithUnckownUser() throws Exception {
        Item item = new Item(1L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 99L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemUpdateAvailable() throws Exception {
        String jsonItem = "{\"available\":\"false\"}";
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("false"));
    }

    @Test
    public void shouldItemUpdateDescription() throws Exception {
        String jsonItem = "{\"description\":\"Аккумуляторная дрель + аккумулятор\"}";
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель + аккумулятор"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void shouldItemUpdateName() throws Exception {
        String jsonItem = "{\"name\":\"Аккумуляторная дрель\"}";
        Long userId = 1L;

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(jsonPath("$.name").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void shouldGetItemByHeader() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value("true"));

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        User userNew = new User(2L, "userNew", "userNew@userNew.com");
        String jsonUser = objectMapper.writeValueAsString(userNew);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        Long userIdNew = 2L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userIdNew)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userIdNew))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Дрель+"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].available").value("false"));

    }

    @Test
    public void shouldGetItemByHeaderWhenTwoItem() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value("true"));

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, false, null);
        String jsonItem = objectMapper.writeValueAsString(item);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Дрель+"))
                .andExpect(jsonPath("$[1].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[1].available").value("false"));
    }

    @Test
    public void shouldSearchItemByText() throws Exception {
        Long userId = 1L;
        String text = "аккУМУляторная";

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());


        mockMvc.perform(get("/items/search?text="+ text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Дрель+"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].available").value("true"));

    }

    @Test
    public void shouldSearchItemByTextForTwoItem() throws Exception {
        Long userId = 1L;
        String text = "дРелЬ";

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());


        mockMvc.perform(get("/items/search?text="+ text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Дрель+"))
                .andExpect(jsonPath("$[1].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[1].available").value("true"));

    }

    @Test
    public void shouldSearchItemByTextWhenEmpty() throws Exception {
        Long userId = 1L;
        String text = "";

        Item item = new Item(2L, "Дрель+", "Аккумуляторная дрель", user, true, null);
        String jsonItem = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());


        mockMvc.perform(get("/items/search?text="+ text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))) ;

    }
}

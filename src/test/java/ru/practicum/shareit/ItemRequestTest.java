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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestTest {

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

        user = new User(2L, "userNew", "userNew@userNew.com");
        String jsonUserNew = objectMapper.writeValueAsString(user);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUserNew));
    }

    @Test
    public void shouldItemRequestWithoutUser() throws Exception {

        ItemRequestDto item = new ItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 99L;

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
        Long userId = 99L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithoutRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetItemRequestWithoutPaginationParams() throws Exception {
        Long userId = 1L;

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

        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldItemRequestAdd() throws Exception {

        ItemRequestDto item = new ItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 1L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"))
                .andExpect(jsonPath("$.items", hasSize(0)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться щёткой для обуви"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items", hasSize(0)));


    }

    @Test
    public void shouldItemRequestAddItemWithRequest() throws Exception {

        Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", user, true, 1L);
        ItemRequestDto itemRequestDto = new ItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
        String jsonItem = objectMapper.writeValueAsString(item);
        String jsonItemRequest = objectMapper.writeValueAsString(itemRequestDto);
        Long userId = 1L;
        Long ownerId = 2L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItemRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"))
                .andExpect(jsonPath("$.items", hasSize(0)));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Щётка для обуви"))
                .andExpect(jsonPath("$.description").value("Стандартная щётка для обуви"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1L));


        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться щёткой для обуви"))
                .andExpect(jsonPath("$[0].created").isNotEmpty())
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }
}

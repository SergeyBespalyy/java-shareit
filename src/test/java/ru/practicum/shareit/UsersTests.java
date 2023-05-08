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
public class UsersTests {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        User user = new User(1L, "user", "user@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));
    }

    @Test
    public void shouldGetUserById() throws Exception {
        Integer idUser = 1;
        mockMvc.perform(get("/users/{id}", idUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }


    @Test
    public void shouldAdUserPostWhenFailName() throws Exception {
        User user = new User(2L, "", "user@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldAdUserPostWhenDuplicateEmail() throws Exception {
        User user = new User(2L, "user", "user@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldAdUserPostWhenFailEmail() throws Exception {
        User user = new User(2L, "user", "");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldUpdatePatchUserWhenStatus200() throws Exception {
        User user = new User(1L, "update", "update@user.com");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("update@user.com"))
                .andExpect(jsonPath("$.name").value("update"));
    }

    @Test
    public void shouldUpdatePatchUserName() throws Exception {
        String jsonUser = "{\"name\":\"updateName\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("updateName"));
    }

    @Test
    public void shouldUpdatePatchUserEmail() throws Exception {
        String jsonUser = "{\"email\":\"updateName@user.com\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updateName@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        User user = new User(2L, "userNew", "userNew@userNew.com");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }


}

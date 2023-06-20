package ru.practicum.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.server.user.UserController;
import ru.practicum.server.user.UserService;
import ru.practicum.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    UserDto userDto = new UserDto(1L, "user", "user@user.com");

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void shouldCreate() throws Exception {
        when(userService.create(any())).thenReturn(userDto);

        String jsonUser = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }


    @Test
    public void shouldGetUserById() throws Exception {
        when(userService.getById(any())).thenReturn(userDto);

        Integer idUser = 1;
        mockMvc.perform(get("/users/{id}", idUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@user.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    public void shouldGetAll() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto, userDto, userDto));

        mockMvc.perform(get("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    public void shouldUpdatePatchUserWhenStatus200() throws Exception {
        UserDto user = new UserDto(1L, "update", "update@user.com");
        String jsonUser = objectMapper.writeValueAsString(userDto);

        when(userService.update(anyLong(), any())).thenReturn(user);

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

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdatePatchUserEmail() throws Exception {
        String jsonUser = "{\"email\":\"updateName@user.com\"}";

        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}

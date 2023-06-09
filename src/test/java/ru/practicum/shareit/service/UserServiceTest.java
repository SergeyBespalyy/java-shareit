package ru.practicum.shareit.service;

/**
 * UNIT-тесты SpringBoot, уровень сервиса, запись в БД и интеграционное тестирование
 */

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    public void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setName("Test tes");

        // When
        UserDto createdUserDto = userService.create(userDto);

        // Then
        assertNotNull(createdUserDto.getId());
        assertEquals(userDto.getEmail(), createdUserDto.getEmail());
        assertEquals(userDto.getName(), createdUserDto.getName());

        User createdUser = userRepository.findById(createdUserDto.getId()).orElse(null);
        assertNotNull(createdUser);
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        assertEquals(userDto.getName(), createdUser.getName());
    }

    @Test
    @Sql("/data/test-users.sql") // Загрузить тестовые данные из файла test-users.sql
    public void testGetAllUsers() {
        List<UserDto> userList = userService.getAll();

        assertEquals(3, userList.size());

        assertEquals("User 1", userList.get(0).getName());
        assertEquals("User 2", userList.get(1).getName());
        assertEquals("User 3", userList.get(2).getName());
    }

    @Test
    public void testGetById() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Long userId = user.getId();

        UserDto userDto = userService.getById(userId);

        assertNotNull(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Long userId = user.getId();

        Map<Object, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("name", "Updated User");
        fieldsToUpdate.put("email", "updated@example.com");

        UserDto updatedUserDto = userService.update(userId, fieldsToUpdate);

        assertNotNull(updatedUserDto);
        assertEquals("Updated User", updatedUserDto.getName());
        assertEquals("updated@example.com", updatedUserDto.getEmail());
    }

    @Test
    public void testDelete() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        Long userId = user.getId();

        userService.delete(userId);

        assertFalse(userRepository.existsById(userId));
    }
}
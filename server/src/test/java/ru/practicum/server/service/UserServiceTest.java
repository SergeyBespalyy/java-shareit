package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;
import ru.practicum.server.user.UserService;
import ru.practicum.server.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT-тесты SpringBoot, уровень сервиса, запись в БД и интеграционное тестирование
 */

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        Assertions.assertEquals(userDto.getEmail(), createdUserDto.getEmail());
        Assertions.assertEquals(userDto.getName(), createdUserDto.getName());

        User createdUser = userRepository.findById(createdUserDto.getId()).orElse(null);
        assertNotNull(createdUser);
        Assertions.assertEquals(userDto.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(userDto.getName(), createdUser.getName());
    }

    @Test
    public void testCreateUserDuplucateEmail() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setName("Test tes");

        UserDto userDtoDuplicate = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setName("Test tes");

        UserDto createdUserDto = userService.create(userDto);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.create(userDtoDuplicate);
        });
    }

    @Test
    @Sql("/data/test-users.sql") // Загрузить тестовые данные из файла test-users.sql
    public void testGetAllUsers() {
        List<UserDto> userList = userService.getAll();

        assertEquals(3, userList.size());

        Assertions.assertEquals("User 1", userList.get(0).getName());
        Assertions.assertEquals("User 2", userList.get(1).getName());
        Assertions.assertEquals("User 3", userList.get(2).getName());
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
        Assertions.assertEquals(user.getEmail(), userDto.getEmail());
        Assertions.assertEquals(user.getName(), userDto.getName());
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
        Assertions.assertEquals("Updated User", updatedUserDto.getName());
        Assertions.assertEquals("updated@example.com", updatedUserDto.getEmail());
    }

    @Test
    public void testDelete() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        userRepository.save(user);

        Long userId = user.getId();

        userService.delete(userId);

        Assertions.assertFalse(userRepository.existsById(userId));
    }
}
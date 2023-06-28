package ru.practicum.server.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import ru.practicum.server.exceptions.ValidationIdException;
import ru.practicum.server.user.dto.UserDto;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс описывает UserService, с основной логикой
 */

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto create(UserDto dto) {
        User user = UserMapper.toUser(dto);
        User newUser = userRepository.save(user);
        UserDto userDto =  UserMapper.toUserDto(newUser);
        return userDto;
    }

    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ValidationIdException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto update(Long id, Map<Object, Object> fields) {
        User user = userRepository.findById(id).orElseThrow(() -> new ValidationIdException("Пользователь не найден"));
        fields.forEach((key, value) -> {
            if (!key.equals("id")) {
                Field field = ReflectionUtils.findField(User.class, (String) key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
            }
        });
        User newUser = userRepository.save(user);
        return UserMapper.toUserDto(newUser);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}

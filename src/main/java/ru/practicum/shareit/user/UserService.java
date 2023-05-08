package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс описывает UserService, с основной логикой
 */

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(UserDto dto) {
        User user = UserMapper.toUser(dto);
        User newUser = userRepository.save(user);
        return UserMapper.toUserDto(newUser);
    }

    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        User user = userRepository.findById(id);
        return UserMapper.toUserDto(user);
    }

    public UserDto update(Long id, UserDto dto) {
        User user = UserMapper.toUser(dto);
        User newUser = userRepository.update(id, user);
        return UserMapper.toUserDto(newUser);
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

}

package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * Класс описывает UserMapper, переводит USER в ДТО и обратно
 */

public class UserMapper {
    public static UserDto toUserDto(User newUser) {
        return UserDto.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();
    }

    public static User toUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}

package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * Класс описывает interface UserRepository хранение в базе данных
 */

public interface UserRepository {
    User save(User user);

    List<User> findAll();

    User findById(Long id);

    User update(Long id, User user);

    void delete(Long id);
}

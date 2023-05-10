package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Класс описывает UserRepositoryInMemory хранение в памяти
 */

@Repository
@RequiredArgsConstructor
public class UserRepositoryInMemory implements UserRepository {
    private final AtomicLong id = new AtomicLong(0L);
    private final Map<String, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        if (userMap.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с такой почтой уже существует");
        }
        Long idUser = id.incrementAndGet();

        user.setId(idUser);
        userMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User findById(Long id) {
        return userMap.values().stream()
                .filter(val -> val.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ValidationIdException("Пользователь не найден"));
    }

    @Override
    public User update(Long id, User user) {
        User oldUser = userMap.values().stream()
                .filter(val -> val.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ValidationIdException("Пользователь не найден"));
        String oldKey = oldUser.getEmail();
        if (userMap.containsKey(user.getEmail()) && !oldKey.equals(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с такой почтой уже существует");
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }


        userMap.remove(oldKey);
        userMap.put(oldUser.getEmail(), oldUser);

        return oldUser;
    }

    @Override
    public void delete(Long id) {
        User oldUser = userMap.values().stream()
                .filter(val -> val.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ValidationIdException("Пользователь не найден"));
        userMap.remove(oldUser.getEmail());
    }
}

package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * Класс описывает interface ItemRepository хранение в базе данных
 */

public interface ItemRepository {
    Item save(Item item);

    List<Item> findAll(User user);

    Item findById(Long id);

    Item update(Long id, Item item);

    void delete(Long id);

    List<Item> search(String text);
}

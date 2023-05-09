package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Класс описывает ItemRepositoryInMemory хранение в оперативной памяти
 */

@Repository
@Slf4j
public class ItemRepositoryInMemory implements ItemRepository {
    private final AtomicLong id = new AtomicLong(0L);
    private final Map<Long, Item> itemMap = new HashMap<>();

    @Override
    public Item save(Item item) {
        Long itemId = id.incrementAndGet();
        itemMap.put(itemId, item);
        item.setId(itemId);
        return item;
    }

    @Override
    public List<Item> findAll(User user) {
        return itemMap.values().stream().filter(item -> item.getOwner().equals(user)).collect(Collectors.toList());
    }

    @Override
    public Item findById(Long id) {
        return itemMap.get(id);
    }

    @Override
    public Item update(Long id, Item item) {
        Item oldItem = itemMap.get(id);

        if (!oldItem.getOwner().equals(item.getOwner())) {
            log.warn("Нет доступа. Id = {} у этой вещи не найден!", item.getId());
            throw new ValidationIdException("Нет доступа. Такой Id у этой вещи не найден!");
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        itemMap.put(id, oldItem);

        return oldItem;
    }

    @Override
    public void delete(Long id) {
        itemMap.remove(id);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> itemList = new ArrayList<>();
        if (text.isBlank()) {
            return itemList;
        }

        for (Map.Entry<Long, Item> entry : itemMap.entrySet()) {
            Item item = entry.getValue();

            if (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)) {
                if (item.getAvailable()) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }
}

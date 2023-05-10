package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Класс описывает ItemService, с основной логикой
 */

@AllArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService service;

    public ItemDto create(ItemDto dto, Long userId) {
        User user = UserMapper.toUser(service.getById(userId));
        Item item = ItemMapper.toItem(dto, user);
        Item newItem = itemRepository.save(item);
        return ItemMapper.toItemDto(newItem);
    }

    public List<ItemDto> getAll(Long userId) {
        User user = UserMapper.toUser(service.getById(userId));
        List<Item> itemList = itemRepository.findAll(user);
        return itemList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(Long id, ItemDto dto, Long userId) {
        User user = UserMapper.toUser(service.getById(userId));
        Item item = ItemMapper.toItem(dto, user);
        Item newItem = itemRepository.update(id, item);
        return ItemMapper.toItemDto(newItem);
    }

    public void delete(Long id) {
        itemRepository.delete(id);
    }

    public List<ItemDto> search(String text) {
        List<Item> itemList = itemRepository.search(text.toLowerCase(Locale.ROOT));
        return itemList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}

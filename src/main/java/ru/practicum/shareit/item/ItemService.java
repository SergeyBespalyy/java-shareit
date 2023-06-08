package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.ItemIsNotAvailableForBookingException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс описывает ItemService, с основной логикой
 */

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService service;

    @Transactional
    public ItemResponseDto create(ItemDto dto, Long userId) {
        User user = UserMapper.toUser(service.getById(userId));
        Item item = ItemMapper.toItem(dto, user);
        Item newItem = itemRepository.save(item);

        return ItemMapper.toItemResponseDto(newItem, new ArrayList<>(), new ArrayList<>());
    }

    public List<ItemResponseDto> getAll(Long userId) {
        User user = UserMapper.toUser(service.getById(userId));
        List<Item> itemList = itemRepository.findAllByOwnerOrderById(user);
        List<Long> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> booking = bookingRepository.findAllByOwnerIdAndItemIn(userId, itemIdList);
        List<CommentResponseDto> commentResponseDto = commentRepository.findAllByAndAuthorName(user.getName())
                .stream()
                .map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());

        return itemList.stream()
                .map(item -> ItemMapper.toItemResponseDto(item, booking, commentResponseDto)).collect(Collectors.toList());
    }

    public ItemResponseDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationIdException("Item не найден"));
        List<Booking> booking = bookingRepository.findAllByItemIdAndOwnerId(itemId, userId);
        List<CommentResponseDto> commentResponseDto = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());

        return ItemMapper.toItemResponseDto(item, booking, commentResponseDto);
    }

    @Transactional
    public ItemResponseDto update(Long id, Map<Object, Object> fields, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ValidationIdException("Item не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationIdException("Пользователь не найден");
        }

        fields.forEach((key, value) -> {
            if (!key.equals("id")) {
                Field field = ReflectionUtils.findField(Item.class, (String) key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, item, value);
            }
        });
        Item newItem = itemRepository.save(item);
        return ItemMapper.toItemResponseDto(newItem, new ArrayList<>(), new ArrayList<>());
    }

    @Transactional
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public List<ItemResponseDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                text, text, true);
        return itemList.stream().map(item -> ItemMapper.toItemResponseDto(item, new ArrayList<>(), new ArrayList<>())).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto createComment(CommentDto dto, Long userId, Long itemId) {
        List<Booking> booking = bookingRepository.findAllByBookerIdAndItemIdAndStatusNotAndStartBefore(userId, itemId, Status.REJECTED, LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new ItemIsNotAvailableForBookingException("Вы не можете оставить отзыв, т.к. не бронировали вещь");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ValidationIdException("User не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationIdException("Item не найден"));
        Comment comment = CommentMapper.toComment(dto, user, item);

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }
}

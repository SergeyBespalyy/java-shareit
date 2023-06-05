package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс описывает ItemRequestService, с основной логикой
 */

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestResponseDto create(ItemRequestDto dto, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dto, userId);
        return ItemRequestMapper.toItemRequestResponseDto(requestRepository.save(itemRequest), new ArrayList<>());
    }

    public List<ItemRequestResponseDto> getForUser(Long userId) {
        userService.getById(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorOrderByCreatedDesc(userId);
        return getItemRequestResponseDto(itemRequests);
    }

    public List<ItemRequestResponseDto> getOtherUsers(Long userId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("size или from должен быть больше 0");
        }
        userService.getById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIsNotOrderByCreatedDesc(userId, page);
        return getItemRequestResponseDto(itemRequests);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDto(List<ItemRequest> itemRequests) {
        List<Item> items = itemRepository.findAllByRequestIdIn(
                itemRequests.stream()
                        .map(ItemRequest::getId)
                        .collect(Collectors.toList()));


        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestResponseDto(
                        itemRequest, items.stream()
                                .filter(item -> item.getRequestId().equals(itemRequest.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        userService.getById(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ValidationIdException("Запрос не найден"));
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }
}

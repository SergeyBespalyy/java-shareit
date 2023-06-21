package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.item.dto.ItemDtoShort;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestMapper;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.request.ItemRequestService;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestResponseDto;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository requestRepository;

    @Test
    public void testCreateItemRequest() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test message");

        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.create(itemRequestDto, user.getId());

        assertNotNull(itemRequestResponseDto);
        assertNotNull(itemRequestResponseDto.getId());
        assertNotNull(itemRequestResponseDto.getCreated());
        assertNotNull(itemRequestResponseDto.getItems());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestResponseDto.getDescription());
    }

    @Test
    public void testGetItemRequestsForUser() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        User requestor = new User();
        requestor.setEmail("requestor@requestor.com");
        requestor.setName("Test requestor");
        userRepository.save(requestor);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Bla bla bla")
                .build();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user.getId());


        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(requestor.getId());
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("New item search");
        itemRequest1.setRequestor(requestor.getId());
        requestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(requestor.getId());
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("New item search number Two");
        requestRepository.save(itemRequest2);

        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestService.getForUser(requestor.getId());

        assertNotNull(itemRequestResponseDtos);
        assertEquals(2, itemRequestResponseDtos.size());
        Assertions.assertEquals(2, itemRequest1.getRequestor());
    }

    @Test
    public void testGetOtherUsersItemRequests() {

        User user1 = new User();
        user1.setEmail("test1@test.com");
        user1.setName("Test User 1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setName("Test User 2");
        userRepository.save(user2);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user2);
        itemRepository.save(item2);


        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user2.getId());
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("New item search");
        requestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user2.getId());
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("New item search number Two");
        requestRepository.save(itemRequest2);

        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestService.getOtherUsers(user1.getId(), 0, 10);

        assertNotNull(itemRequestResponseDtos);
        assertNotNull(itemRequestResponseDtos.get(0).getDescription());
        assertEquals(2, itemRequestResponseDtos.size());
    }

    @Test
    public void testGetRequestById() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("New item search");
        requestRepository.save(itemRequest);

        ItemDtoShort itemDtoShort = new ItemDtoShort();
        itemDtoShort.setId(5L);
        itemDtoShort.setName("Ivan");
        itemDtoShort.setDescription("Bla bla vla");
        itemDtoShort.setAvailable(true);
        itemDtoShort.setRequestId(1L);

        ItemDtoShort itemDtoShortTwo = ItemDtoShort.builder()
                .id(5L)
                .name("Petr")
                .description("Bla bla vla")
                .available(true)
                .requestId(1L).build();

        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.getRequestById(user.getId(), itemRequest.getId());
        itemRequestResponseDto.setItems(List.of(ItemMapper.toItemDtoShort(item), itemDtoShort, itemDtoShortTwo));

        assertNotNull(itemRequestResponseDto);
        Assertions.assertEquals(3, itemRequestResponseDto.getItems().size());
        Assertions.assertEquals(itemRequest.getId(), itemRequestResponseDto.getId());
    }
}
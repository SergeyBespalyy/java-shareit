package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired

    private UserRepository userRepository;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        User user = new User(1L, "John Doe", "johndoe@example.com");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Описание для запроса вещи")
                .created(time)
                .requestor(savedUser.getId())
                .build();

        ItemRequest savedItemRequest = requestRepository.save(itemRequest);

        Long itemRequestId = savedItemRequest.getId();

        ItemRequest retrievedItemRequest = entityManager.find(ItemRequest.class, itemRequestId);

        assertThat(retrievedItemRequest).isEqualTo(savedItemRequest);
    }
}

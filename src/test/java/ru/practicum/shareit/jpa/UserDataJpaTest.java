package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired

    private UserRepository userRepository;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        User user = new User(2L, "John Doe", "johndoe@example.com");
        User savedUser = userRepository.save(user);

        Long savedUserId = savedUser.getId();

        User retrievedUser = entityManager.find(User.class, savedUserId);

        assertThat(retrievedUser).isEqualTo(savedUser);
    }

    @Test
    void testUseWithDuplicateEmail() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        User user = new User(2L, "John Doe", "johndoe@example.com");
        User savedUser = userRepository.save(user);

        Long savedUserId = savedUser.getId();

        User retrievedUser = entityManager.find(User.class, savedUserId);

        assertThat(retrievedUser).isEqualTo(savedUser);

        assertThrows(DataIntegrityViolationException.class, () -> {
            User userDuplicate = new User(3L, "New DoeS", "johndoe@example.com");
            User savedUserDuplicate = userRepository.save(user);
        });
    }
}

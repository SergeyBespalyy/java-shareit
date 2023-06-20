package ru.practicum.server.jpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.server.booking.Status;
import ru.practicum.server.item.Comment;
import ru.practicum.server.item.CommentRepository;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
public class CommentDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        User user = new User(1L, "John Doe", "johndoe@example.com");
        User savedUser = userRepository.save(user);

        User booker = new User(2L, "booker Doe", "booker@example.com");
        User savedUserBooker = userRepository.save(booker);

        Item item = new Item(1L, "Веник", "Домашний", savedUser, true, null);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking(1L, savedItem, LocalDateTime.now(), LocalDateTime.now().plusDays(1), savedUserBooker, Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        Comment comment = new Comment(1L, "BlaBlaBla", savedItem, savedUserBooker.getName(), LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        Long savedCommentId = savedComment.getId();

        Comment retrievedComment = entityManager.find(Comment.class, savedCommentId);

        Assertions.assertThat(retrievedComment).isEqualTo(savedComment);
    }
}

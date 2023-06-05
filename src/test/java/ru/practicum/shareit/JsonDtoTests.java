package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс отвечает за тестирование функциональности, связанной с сериализацией и десериализацией JSON ItemRequestDto
 */

@JsonTest
public class JsonDtoTests {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonRequest;
    @Autowired
    private JacksonTester<ItemRequestResponseDto> jsonResponse;

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;
    @Autowired
    private JacksonTester<BookingDtoForItem> jsonBookingDtoForItem;
    @Autowired
    private JacksonTester<BookingResponseDto> jsonBookingResponseDto;
    @Autowired
    private JacksonTester<Comment> jsonComment;
    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Описание для запроса вещи");

        JsonContent<ItemRequestDto> result = jsonRequest.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание для запроса вещи");
    }

    @Test
    void testItemRequestResponseDto() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(
                2L,
                "Отвертка с ручкой",
                time,
                new ArrayList<>());
        JsonContent<ItemRequestResponseDto> result = jsonResponse.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Отвертка с ручкой");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingDto bookingDtoNew = new BookingDto(1L, 1L, start, end, Status.WAITING);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.WAITING.toString());
    }

    @Test
    void testBookingDtoForItem() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingDtoForItem bookingDtoForItemNew = new BookingDtoForItem(1L, start, end, 2L, Status.APPROVED);

        JsonContent<BookingDtoForItem> result = jsonBookingDtoForItem.write(bookingDtoForItemNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.APPROVED.toString());
    }

    @Test
    void testBookingResponseDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingResponseDto bookingResponseDtoNew = new BookingResponseDto(1L, null, start, end, null, Status.REJECTED);

        JsonContent<BookingResponseDto> result = jsonBookingResponseDto.write(bookingResponseDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.REJECTED.toString());
    }

    @Test
    void testComment() throws Exception {
        LocalDateTime created = LocalDateTime.now().plusDays(1);
        Comment comment = new Comment(1L, "Комментарий к букингу", new Item(), "Вася", created);
        CommentDto commentDto = CommentMapper.toCommentDto(comment, new User());

        CommentDto commentDtoNew = new CommentDto(2L, "Комментарий к букингу из ДТО", new Item(), "Petr", created);
        Comment newComment = CommentMapper.toComment(commentDtoNew, new User(), new Item());

        JsonContent<Comment> result = jsonComment.write(newComment);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий к букингу из ДТО");
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();

        JsonContent<CommentDto> resultDto = jsonCommentDto.write(commentDto);

        assertThat(resultDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultDto).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий к букингу");
        assertThat(resultDto).extractingJsonPathValue("$.created").isNotNull();
    }


}

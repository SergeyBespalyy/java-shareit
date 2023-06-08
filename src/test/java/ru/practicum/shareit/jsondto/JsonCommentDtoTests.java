package ru.practicum.shareit.jsondto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс отвечает за тестирование функциональности, связанной с сериализацией и десериализацией JSON ItemRequestDto
 */

@JsonTest
public class JsonCommentDtoTests {

    @Autowired
    private JacksonTester<Comment> jsonComment;
    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;

    @Autowired
    private JacksonTester<CommentResponseDto> jsonCommentResponseDto;


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

    @Test
    void testCommentResponseDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().plusDays(1);
        Comment comment = new Comment(1L, "Комментарий к букингу",
                new Item(2L, "Веник", "Домашний",
                        new User(), true, 3L), "Вася", created);

        CommentResponseDto newComment = CommentMapper.toCommentResponseDto(comment);

        JsonContent<CommentResponseDto> result = jsonCommentResponseDto.write(newComment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий к букингу");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Веник");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Вася");
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();
    }


}

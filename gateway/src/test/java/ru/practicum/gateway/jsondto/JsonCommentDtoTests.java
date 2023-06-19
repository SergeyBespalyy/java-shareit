package ru.practicum.gateway.jsondto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.gateway.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс отвечает за тестирование функциональности, связанной с сериализацией и десериализацией JSON ItemRequestDto
 */

@JsonTest
public class JsonCommentDtoTests {

    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;

    @Test
    void testComment() throws Exception {
        LocalDateTime created = LocalDateTime.now().plusDays(1);

        CommentDto commentDtoNew = CommentDto.builder().text("Комментарий NEW").build();

        JsonContent<CommentDto> result = jsonCommentDto.write(commentDtoNew);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий NEW");

    }
}

package ru.practicum.gateway.jsondto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.gateway.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс отвечает за тестирование функциональности, связанной с сериализацией и десериализацией JSON ItemRequestDto
 */

@JsonTest
public class JsonItemRequestDtoTests {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonRequest;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Описание для запроса вещи");

        JsonContent<ItemRequestDto> result = jsonRequest.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание для запроса вещи");
    }
}

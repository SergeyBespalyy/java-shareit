package ru.practicum.gateway.jsondto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.gateway.request.dto.ItemRequestDto;
import ru.practicum.gateway.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс отвечает за тестирование функциональности, связанной с сериализацией и десериализацией JSON ItemRequestDto
 */

@JsonTest
public class JsonItemRequestDtoTests {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonRequest;
    @Autowired
    private JacksonTester<ItemRequestResponseDto> jsonResponse;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Описание для запроса вещи");

        JsonContent<ItemRequestDto> result = jsonRequest.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание для запроса вещи");
    }

    @Test
    void testItemRequestResponseDto() throws Exception {
        LocalDateTime time = LocalDateTime.now().withNano(000000);

        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(2L, "Отвертка с ручкой", time, new ArrayList<>());
        JsonContent<ItemRequestResponseDto> result = jsonResponse.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Отвертка с ручкой");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }
}

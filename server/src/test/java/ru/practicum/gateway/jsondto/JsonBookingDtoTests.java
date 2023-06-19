package ru.practicum.gateway.jsondto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.gateway.booking.Status;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingDtoForItem;
import ru.practicum.gateway.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс отвечает за тестирование функциональности, связанной с сериализацией и десериализацией JSON ItemRequestDto
 */

@JsonTest
public class JsonBookingDtoTests {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;
    @Autowired
    private JacksonTester<BookingDtoForItem> jsonBookingDtoForItem;
    @Autowired
    private JacksonTester<BookingResponseDto> jsonBookingResponseDto;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingDto bookingDtoNew = new BookingDto(1L, 1L, start, end, Status.WAITING);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
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
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.APPROVED.toString());
    }

    @Test
    void testBookingResponseDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingResponseDto bookingResponseDtoNew = new BookingResponseDto(1L, null, start, end, null, Status.REJECTED);

        JsonContent<BookingResponseDto> result = jsonBookingResponseDto.write(bookingResponseDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.REJECTED.toString());
    }

    @Test
    void testBookingResponseDtoWithoutBookerAndItem() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(000000);
        LocalDateTime end = LocalDateTime.now().withNano(000000).plusDays(1);

        BookingResponseDto bookingResponseDtoNew = new BookingResponseDto(1L, new BookingResponseDto.Item(), start, end, new BookingResponseDto.Booker(), Status.REJECTED);

        JsonContent<BookingResponseDto> result = jsonBookingResponseDto.write(bookingResponseDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.REJECTED.toString());
    }

    @Test
    void testBookingResponseDtoWithoutData() throws Exception {

        BookingResponseDto bookingResponseDtoNew = new BookingResponseDto();

        JsonContent<BookingResponseDto> result = jsonBookingResponseDto.write(bookingResponseDtoNew);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);

    }
}

package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * Класс описывает модель Booking
 * */
@Data
public class Booking {
    private Long id;
    private Item item;
    private LocalDate start;
    private LocalDate end;
    private User owner;
    private User booker;
    private Status status;
}

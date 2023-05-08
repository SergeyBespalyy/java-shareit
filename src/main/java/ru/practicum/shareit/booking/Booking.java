package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    private Item item;
    private LocalDate startRent;
    private LocalDate endRent;
    private User owner;
    private User tenant;
    private Boolean isConfirmation;
}

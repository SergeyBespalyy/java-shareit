package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Long id;
    private String name;
    private String description;
    private User ownerRequest;
}
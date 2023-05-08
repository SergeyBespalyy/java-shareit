package ru.practicum.shareit.request;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Long id;
    private String name;
    private String description;
}
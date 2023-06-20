package ru.practicum.server.item;

import lombok.*;
import ru.practicum.server.user.User;

import javax.persistence.*;

/**
 * Класс описывает модель Item
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private User owner;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "request")
    private Long requestId;

}

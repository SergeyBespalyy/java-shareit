package ru.practicum.gateway.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Класс описывает interface ItemRepository хранение в базе данных
 */

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByAndAuthorName(String author);

}

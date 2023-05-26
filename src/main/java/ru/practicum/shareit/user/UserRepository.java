package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Класс описывает interface UserRepository хранение в базе данных
 */

public interface UserRepository extends JpaRepository<User, Long> {
}

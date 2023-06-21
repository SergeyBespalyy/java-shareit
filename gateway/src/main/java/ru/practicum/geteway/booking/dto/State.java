package ru.practicum.geteway.booking.dto;

import java.util.Optional;

/**
 * Класс описывает State Booking
 * ALL - все,
 * WAITING - ожидающие подтверждения,
 * CURRENT — текущие,
 * PAST — завершённые,
 * REJECTED — отклонённые,
 * FUTURE — будущие.
 */
public enum State {
    ALL, WAITING, CURRENT, PAST, REJECTED, FUTURE;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
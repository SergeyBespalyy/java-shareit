package ru.practicum.shareit.booking;
/**
 * Класс описывает State Booking
 * ALL - все,
 * WAITING - ожидающие подтверждения,
 * CURRENT — текущие,
 * PAST — завершённые,
 * REJECTED — отклонённые,
 * FUTURE — будущие.
 * */
public enum State {
    ALL, WAITING, CURRENT, PAST, REJECTED, FUTURE
}
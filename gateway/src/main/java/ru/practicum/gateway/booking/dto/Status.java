package ru.practicum.gateway.booking.dto;

/**
 * Класс описывает Status Booking
 * WAITING — новое бронирование, ожидает одобрения/подтверждения,
 * APPROVED — бронирование подтверждено владельцем,
 * REJECTED — бронирование отклонено владельцем,
 * CANCELED — бронирование отменено создателем.
* */
public enum Status {
    WAITING, APPROVED, REJECTED, CANCELED
}

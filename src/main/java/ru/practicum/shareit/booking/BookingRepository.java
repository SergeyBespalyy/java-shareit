package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "WHERE b.id = ?1 " +
            "AND b.owner.id = ?2")
    Booking findBookingOwner(Long bookingId, Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "WHERE b.id = ?1 " +
            "AND (b.owner.id = ?2 OR b.booker.id = ?2)")
    Booking findBookingOwnerOrBooker(Long bookingId, Long ownerId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime time, LocalDateTime time2);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByOwnerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime time, LocalDateTime time2);

    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItemIdAndOwnerId(Long itemId, Long ownerId);

    List<Booking> findAllByOwnerIdAndItemIn(Long ownerId, List<Item> items);

    List<Booking> findAllByBookerIdAndItemIdAndStatusNotAndStartBefore(Long bookerId, Long itemId, Status status, LocalDateTime time);

}

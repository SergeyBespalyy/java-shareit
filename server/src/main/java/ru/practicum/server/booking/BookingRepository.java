package ru.practicum.server.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "JOIN b.item AS i " +
            "WHERE b.id = ?1 " +
            "AND i.owner.id = ?2")
    Booking findBookingOwner(Long bookingId, Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "JOIN b.item AS i " +
            "WHERE b.id = ?1 " +
            "AND (i.owner.id = ?2 OR b.booker.id = ?2)")
    Booking findBookingOwnerOrBooker(Long bookingId, Long ownerId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime time, LocalDateTime time2, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start < ?2 AND b.end > ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime time, LocalDateTime time2, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.id = ?1 " +
            "AND i.owner.id = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByItemIdAndOwnerId(Long itemId, Long ownerId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE  i.owner.id = ?1 " +
            "AND i.id IN (?2) ")
    List<Booking> findAllByOwnerIdAndItemIn(Long ownerId, List<Long> items);

    List<Booking> findAllByBookerIdAndItemIdAndStatusNotAndStartBefore(Long bookerId, Long itemId, Status status, LocalDateTime time);

}

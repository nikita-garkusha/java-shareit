package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId,
                                    Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp between b.start and b.end")
    List<Booking> findAllByBookerIdAndStateCurrent(Long bookerId,
                                                   Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp > b.end")
    List<Booking> findAllByBookerIdAndStatePast(Long bookerId,
                                                Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp < b.start")
    List<Booking> findAllByBookerIdAndStateFuture(Long bookerId,
                                                  Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId,
                                             Status status,
                                             Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(Long ownerId,
                                   Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp between b.start and b.end")
    List<Booking> findAllByOwnerIdAndStateCurrent(Long ownerId,
                                                  Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp > b.end")
    List<Booking> findAllByOwnerIdAndStatePast(Long ownerId,
                                               Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start")
    List<Booking> findAllByOwnerIdAndStateFuture(Long ownerId,
                                                 Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2 " +
            " order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStatus(Long ownerId,
                                            Status status,
                                            Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId,
                                                                             LocalDateTime localDate,
                                                                             Status status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc(Long itemId,
                                                                           LocalDateTime localDate,
                                                                           Status status);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId,
                                                  Long itemId,
                                                  LocalDateTime dateTime);
}

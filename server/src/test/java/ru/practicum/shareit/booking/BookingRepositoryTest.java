package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User userFromDb1;
    private User userFromDb2;
    private Item itemFromDb1;
    private Item itemFromDb2;
    private Booking bookingFromDb;
    private Booking nextFromDb;
    private Booking lastFromDb;
    private Booking waitingFromDb;
    private Booking rejectedFromDb;

    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User();
        user1.setName("vityok");
        user1.setEmail("vityok@mail.com");
        userFromDb1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("dsafd");
        user2.setEmail("dafda@mail.com");
        userFromDb2 = userRepository.save(user2);

        Item item1 = new Item();
        item1.setName("Кирпич");
        item1.setDescription("Шлакоблокунь");
        item1.setAvailable(true);
        item1.setOwner(userFromDb1);
        itemFromDb1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("sdfsfds");
        item2.setDescription("dsfdsf");
        item2.setAvailable(true);
        item2.setOwner(userFromDb2);
        itemFromDb2 = itemRepository.save(item2);

        Booking booking = new Booking();
        booking.setItem(itemFromDb2);
        booking.setBooker(userFromDb1);
        booking.setStart(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        booking.setStatus(Status.APPROVED);
        bookingFromDb = bookingRepository.save(booking);

        Booking last = new Booking();
        last.setItem(itemFromDb1);
        last.setBooker(userFromDb2);
        last.setStart(LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.SECONDS));
        last.setEnd(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));
        last.setStatus(Status.APPROVED);
        lastFromDb = bookingRepository.save(last);

        Booking next = new Booking();
        next.setItem(itemFromDb1);
        next.setBooker(userFromDb2);
        next.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        next.setEnd(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS));
        next.setStatus(Status.APPROVED);
        nextFromDb = bookingRepository.save(next);

        Booking waiting = new Booking();
        waiting.setItem(itemFromDb1);
        waiting.setBooker(userFromDb2);
        waiting.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        waiting.setEnd(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS));
        waiting.setStatus(Status.WAITING);
        waitingFromDb = bookingRepository.save(waiting);

        Booking rejected = new Booking();
        rejected.setItem(itemFromDb1);
        rejected.setBooker(userFromDb2);
        rejected.setStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
        rejected.setEnd(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS));
        rejected.setStatus(Status.REJECTED);
        rejectedFromDb = bookingRepository.save(rejected);

        pageable = PageRequest.of(1 / 20, 20, Sort.by("start").descending());
    }

    @Test
    void findAllByBookerId_return1BookingByUser1_added5Bookings() {
        assertEquals(List.of(bookingFromDb), bookingRepository.findAllByBookerId(userFromDb1.getId(), pageable));
        assertEquals(1, bookingRepository.findAllByBookerId(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByBookerId_return4BookingsByUser2_added5Bookings() {
        assertEquals(List.of(nextFromDb, waitingFromDb, rejectedFromDb, lastFromDb),
                bookingRepository.findAllByBookerId(userFromDb2.getId(), pageable));
        assertEquals(4, bookingRepository.findAllByBookerId(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByBookerId_returnEmptyByUnknownUser_added5Bookings() {
        assertEquals(List.of(), bookingRepository.findAllByBookerId(999L, pageable));
        assertEquals(0, bookingRepository.findAllByBookerId(999L, pageable).size());
    }

    @Test
    void findAllByBookerIdAndStateCurrent_return1BookingByUser1_added5Bookings() {
        assertEquals(List.of(bookingFromDb),
                bookingRepository.findAllByBookerIdAndStateCurrent(userFromDb1.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStateCurrent(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByBookerIdAndStateCurrent_returnEmptyByWrongUser_added5Bookings() {
        assertEquals(List.of(),
                bookingRepository.findAllByBookerIdAndStateCurrent(userFromDb2.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByBookerIdAndStateCurrent(userFromDb2.getId(), pageable).size());
    }


    @Test
    void findAllByBookerIdAndStatePast_return1BookingByUser2_added5Bookings() {
        assertEquals(List.of(lastFromDb),
                bookingRepository.findAllByBookerIdAndStatePast(userFromDb2.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStatePast(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByBookerIdAndStatePast_returnEmptyByWrongUser_added5Bookings() {
        assertEquals(List.of(),
                bookingRepository.findAllByBookerIdAndStatePast(userFromDb1.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByBookerIdAndStatePast(userFromDb1.getId(), pageable).size());
    }


    @Test
    void findAllByBookerIdAndStateFuture_return3BookingByUser2_added5Bookings() {
        assertEquals(List.of(nextFromDb, waitingFromDb, rejectedFromDb),
                bookingRepository.findAllByBookerIdAndStateFuture(userFromDb2.getId(), pageable));
        assertEquals(3,
                bookingRepository.findAllByBookerIdAndStateFuture(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByBookerIdAndStateFuture_returnEmptyByWrongUser_added5Bookings() {
        assertEquals(List.of(),
                bookingRepository.findAllByBookerIdAndStateFuture(userFromDb1.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByBookerIdAndStateFuture(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByBookerIdAndStatus_return1WaitingBookingByUser2_added5Bookings() {
        assertEquals(List.of(waitingFromDb),
                bookingRepository.findAllByBookerIdAndStatus(userFromDb2.getId(), Status.WAITING, pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStatus(userFromDb2.getId(), Status.WAITING, pageable).size());
    }

    @Test
    void findAllByBookerIdAndStatus_return1RejectedBookingByUser2_added5Bookings() {
        assertEquals(List.of(rejectedFromDb),
                bookingRepository.findAllByBookerIdAndStatus(userFromDb2.getId(), Status.REJECTED, pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStatus(userFromDb2.getId(), Status.REJECTED, pageable).size());
    }

    @Test
    void findAllByOwnerId_return1BookingByUser2_added5Bookings() {
        assertEquals(List.of(bookingFromDb),
                bookingRepository.findAllByOwnerId(userFromDb2.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerId(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerId_return4BookingByUser1_added5Bookings() {
        assertEquals(List.of(nextFromDb, waitingFromDb, rejectedFromDb, lastFromDb),
                bookingRepository.findAllByOwnerId(userFromDb1.getId(), pageable));
        assertEquals(4,
                bookingRepository.findAllByOwnerId(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerId_returnEmptyByUnknownUser_added5Bookings() {
        assertEquals(List.of(), bookingRepository.findAllByOwnerId(999L, pageable));
        assertEquals(0, bookingRepository.findAllByOwnerId(999L, pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStateCurrent_returnEmptyByUser1_added5Bookings() {
        assertEquals(List.of(),
                bookingRepository.findAllByOwnerIdAndStateCurrent(userFromDb1.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByOwnerIdAndStateCurrent(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStateCurrent_return1BookingByUser2_added5Bookings() {
        assertEquals(List.of(bookingFromDb),
                bookingRepository.findAllByOwnerIdAndStateCurrent(userFromDb2.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStateCurrent(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStatePast_return1BookingByUser1_added5Bookings() {
        assertEquals(List.of(lastFromDb),
                bookingRepository.findAllByOwnerIdAndStatePast(userFromDb1.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStatePast(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStatePast_returnEmptyByUser2_added5Bookings() {
        assertEquals(List.of(),
                bookingRepository.findAllByOwnerIdAndStatePast(userFromDb2.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByOwnerIdAndStatePast(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStateFuture_return1BookingByUser1_added5Bookings() {
        assertEquals(List.of(nextFromDb, waitingFromDb, rejectedFromDb),
                bookingRepository.findAllByOwnerIdAndStateFuture(userFromDb1.getId(), pageable));
        assertEquals(3,
                bookingRepository.findAllByOwnerIdAndStateFuture(userFromDb1.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStateFuture_returnEmptyByUser2_added5Bookings() {
        assertEquals(List.of(),
                bookingRepository.findAllByOwnerIdAndStateFuture(userFromDb2.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByOwnerIdAndStateFuture(userFromDb2.getId(), pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStatus_return1WaitingBookingByUser1_added5Bookings() {
        assertEquals(List.of(waitingFromDb),
                bookingRepository.findAllByOwnerIdAndStatus(userFromDb1.getId(), Status.WAITING, pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStatus(userFromDb1.getId(), Status.WAITING, pageable).size());
    }

    @Test
    void findAllByOwnerIdAndStatus_return1RejectedBookingByUser1_added5Bookings() {
        assertEquals(List.of(rejectedFromDb),
                bookingRepository.findAllByOwnerIdAndStatus(userFromDb1.getId(), Status.REJECTED, pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStatus(userFromDb1.getId(), Status.REJECTED, pageable).size());
    }

    @Test
    void findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc_return1LastBookingByUser1_added5Bookings() {
        assertEquals(lastFromDb, bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemFromDb1.getId(),
                        LocalDateTime.now(),
                        Status.APPROVED).get());
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc_return1NextBookingByUser1_added5Bookings() {
        assertEquals(nextFromDb, bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc(itemFromDb1.getId(),
                        LocalDateTime.now(),
                        Status.APPROVED).get());
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBefore_returnTrueByUser2andItem1_added5Bookings() {
        assertTrue(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userFromDb2.getId(),
                itemFromDb1.getId(),
                LocalDateTime.now()));
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBefore_returnFalseByUser1andItem2_added5Bookings() {
        assertFalse(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userFromDb1.getId(),
                itemFromDb2.getId(),
                LocalDateTime.now()));
    }
}
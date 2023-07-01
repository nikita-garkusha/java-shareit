package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking1;
    private BookingFullDto bookingFullDto1;
    private BookingInputDto bookingInputDto1;
    private User user1;
    private User user2;
    private Item item1;

    private Pageable pageable;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "sadsa", "dsads@dsads.ru");
        user2 = new User(2L, "sadsadsa", "dsasdadsds@ddsadssads.ru");
        ItemRequest itemRequest1 = new ItemRequest(1L, "dswads", user1,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        item1 = new Item(1L, "sdad", "dswads", true, user1, itemRequest1);
        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        booking1.setEnd(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS));
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setStatus(Status.WAITING);
        bookingFullDto1 = BookingMapper.mapToFullDto(booking1);
        bookingInputDto1 = new BookingInputDto(booking1.getStart(), booking1.getEnd(), booking1.getItem().getId());
        pageable = PageRequest.of(1 / 20, 20, Sort.by("start").descending());
    }

    @Test
    void getByBookerId_throwUnknownStateException_wrongState() {
        assertThrows(UnknownStateException.class, () ->
                bookingService.getByBookerId(1L, "dsad", 1, 20));
    }

    @Test
    void getByBookerId_IllegalArgumentException_wrongFromAndSize() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getByBookerId(1L, "ALL", 0, 0));
    }

    @Test
    void getByBookerId_return1Booking_allState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByBookerId(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "ALL", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByBookerId_return1Booking_currentState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStateCurrent(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "CURRENT", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByBookerId_return1Booking_pastState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStatePast(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "PAST", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByBookerId_return1Booking_futureState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByBookerIdAndStateFuture(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "FUTURE", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByBookerId_return1Booking_waitingState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStatus(user1.getId(), Status.WAITING, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "WAITING", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByBookerId_return1Booking_rejectedState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStatus(user1.getId(), Status.REJECTED, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "REJECTED", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByOwnerId_throwUnknownStateException_wrongState() {
        assertThrows(UnknownStateException.class, () ->
                bookingService.getByOwnerId(1L, "dsad", 1, 20));
    }

    @Test
    void getByOwnerId_IllegalArgumentException_wrongFromAndSize() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getByOwnerId(1L, "ALL", 0, 0));
    }


    @Test
    void getByOwnerId_return1Booking_allState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByOwnerId(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "ALL", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByOwnerId_return1Booking_currentState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStateCurrent(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "CURRENT", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByOwnerId_return1Booking_pastState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStatePast(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "PAST", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByOwnerId_return1Booking_futureState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByOwnerIdAndStateFuture(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "FUTURE", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByOwnerId_return1Booking_waitingState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStatus(user1.getId(), Status.WAITING, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "WAITING", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getByOwnerId_return1Booking_rejectedState() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStatus(user1.getId(), Status.REJECTED, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "REJECTED", 1, 20)).asList()
                .contains(bookingFullDto1);
    }

    @Test
    void getById_returnBooking_rightBookingId() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(userService.getUserById(user1.getId())).thenReturn(user1);

        assertThat(bookingService.getById(user1.getId(), booking1.getId())).isEqualTo(bookingFullDto1);
    }

    @Test
    void create_returnBooking_rightBooking() {
        when(userService.getUserById(user2.getId())).thenReturn(user2);
        when(itemService.getItemById(item1.getId())).thenReturn(item1);
        when(bookingRepository.save(any())).thenReturn(booking1);

        assertThat(bookingService.create(user2.getId(), bookingInputDto1)).isEqualTo(bookingFullDto1);
    }

    @Test
    void approve_returnApproveBooking_waitingBooking() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemService.getItemById(item1.getId())).thenReturn(item1);
        bookingFullDto1.setStatus(Status.APPROVED);

        assertThat(bookingService.approve(user1.getId(), booking1.getId(), true)).isEqualTo(bookingFullDto1);
    }

    @Test
    void approve_returnRejectedBooking_waitingBooking() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemService.getItemById(item1.getId())).thenReturn(item1);
        bookingFullDto1.setStatus(Status.REJECTED);

        assertThat(bookingService.approve(user1.getId(), booking1.getId(), false)).isEqualTo(bookingFullDto1);
    }

    @Test
    void getBookingById_return1Booking_rightBookingId() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));

        assertThat(bookingService.getBookingById(booking1.getId())).isEqualTo(booking1);
    }
}
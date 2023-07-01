package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIT {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private UserFullDto userFullDto1;
    private UserFullDto userFullDto2;
    private BookingFullDto currentBookingFullDto;
    private BookingFullDto pastBookingFullDto;
    private BookingFullDto futureBookingFullDto;
    private BookingFullDto waitingBookingFullDto;
    private BookingFullDto rejectedBookingFullDto;
    private ItemFullDto itemFullDto2;

    @BeforeEach
    void beforeEach() {
        UserInputDto userInputDto1 = new UserInputDto(null, "swadsa", "dsadsa");
        UserInputDto userInputDto2 = new UserInputDto(null, "swsadadsa", "dssadadsa");
        userFullDto1 = userService.create(userInputDto1);
        userFullDto2 = userService.create(userInputDto2);

        ItemInputDto itemInputDto1 =
                new ItemInputDto(null, "asdfgh", "asdfghdfgh", true, null);
        ItemInputDto itemInputDto2 =
                new ItemInputDto(null, "asdfghjk", "zxcvbnmjk", true, null);

        ItemFullDto itemFullDto1 = itemService.create(userFullDto1.getId(), itemInputDto1);
        itemFullDto2 = itemService.create(userFullDto2.getId(), itemInputDto2);
        itemFullDto1.setComments(new ArrayList<>());
        itemFullDto2.setComments(new ArrayList<>());

        LocalDateTime currentStart = LocalDateTime.now().minusDays(1);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(2);
        BookingInputDto currentBookingInputDto = new BookingInputDto(currentStart, currentEnd, itemFullDto2.getId());

        LocalDateTime pastStart = LocalDateTime.now().minusDays(3);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        BookingInputDto pastBookingInputDto = new BookingInputDto(pastStart, pastEnd, itemFullDto2.getId());

        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(3);
        BookingInputDto futureBookingInputDto = new BookingInputDto(futureStart, futureEnd, itemFullDto2.getId());

        BookingInputDto waitingBookingInputDto = new BookingInputDto(futureStart, futureEnd, itemFullDto1.getId());
        BookingInputDto rejectedBookingInputDto = new BookingInputDto(futureStart, futureEnd, itemFullDto1.getId());

        currentBookingFullDto = bookingService.create(userFullDto1.getId(), currentBookingInputDto);
        pastBookingFullDto = bookingService.create(userFullDto1.getId(), pastBookingInputDto);
        futureBookingFullDto = bookingService.create(userFullDto1.getId(), futureBookingInputDto);

        waitingBookingFullDto = bookingService.create(userFullDto2.getId(), waitingBookingInputDto);
        rejectedBookingFullDto = bookingService.create(userFullDto2.getId(), rejectedBookingInputDto);
    }

    @Test
    void create_findBooking_added6Bookings() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingInputDto bookingInputDto = new BookingInputDto(start, end, itemFullDto2.getId());

        BookingFullDto bookingFullDto = bookingService.create(userFullDto1.getId(), bookingInputDto);

        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "ALL", 0, 20)).asList()
                .contains(bookingFullDto);
    }


    @Test
    void getByBookerId_returnBookings_added5Booking() {
        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "ALL", 0, 20))
                .asList().containsExactly(futureBookingFullDto, currentBookingFullDto, pastBookingFullDto);

        assertThat(bookingService.getByBookerId(userFullDto2.getId(), "ALL", 0, 20))
                .asList().containsExactly(waitingBookingFullDto, rejectedBookingFullDto);

        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "CURRENT", 0, 20))
                .asList().containsExactly(currentBookingFullDto);

        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "FUTURE", 0, 20))
                .asList().containsExactly(futureBookingFullDto);

        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "PAST", 0, 20))
                .asList().containsExactly(pastBookingFullDto);

        bookingService.approve(userFullDto2.getId(), futureBookingFullDto.getId(), true);
        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "WAITING", 0, 20))
                .asList().containsExactly(currentBookingFullDto, pastBookingFullDto);

        bookingService.approve(userFullDto2.getId(), pastBookingFullDto.getId(), false);
        pastBookingFullDto.setStatus(Status.REJECTED);
        assertThat(bookingService.getByBookerId(userFullDto1.getId(), "REJECTED", 0, 20))
                .asList().containsExactly(pastBookingFullDto);
    }

    @Test
    void getByOwnerId_returnBookings_added5Booking() {
        assertThat(bookingService.getByOwnerId(userFullDto2.getId(), "ALL", 0, 20))
                .asList().containsExactly(futureBookingFullDto, currentBookingFullDto, pastBookingFullDto);

        assertThat(bookingService.getByOwnerId(userFullDto1.getId(), "ALL", 0, 20))
                .asList().containsExactly(waitingBookingFullDto, rejectedBookingFullDto);

        assertThat(bookingService.getByOwnerId(userFullDto2.getId(), "CURRENT", 0, 20))
                .asList().containsExactly(currentBookingFullDto);

        assertThat(bookingService.getByOwnerId(userFullDto2.getId(), "FUTURE", 0, 20))
                .asList().containsExactly(futureBookingFullDto);

        assertThat(bookingService.getByOwnerId(userFullDto2.getId(), "PAST", 0, 20))
                .asList().containsExactly(pastBookingFullDto);

        bookingService.approve(userFullDto2.getId(), futureBookingFullDto.getId(), true);
        assertThat(bookingService.getByOwnerId(userFullDto2.getId(), "WAITING", 0, 20))
                .asList().containsExactly(currentBookingFullDto, pastBookingFullDto);

        bookingService.approve(userFullDto2.getId(), pastBookingFullDto.getId(), false);
        pastBookingFullDto.setStatus(Status.REJECTED);
        assertThat(bookingService.getByOwnerId(userFullDto2.getId(), "REJECTED", 0, 20))
                .asList().containsExactly(pastBookingFullDto);
    }

    @Test
    void getById_return1Booking_bookingIdByCurrentBooking() {
        assertThat(bookingService.getById(userFullDto1.getId(), currentBookingFullDto.getId()))
                .isEqualTo(currentBookingFullDto);
    }

    @Test
    void approve_returnApprovedAndRejectedBooking_addWaitingBookings() {
        assertThat(bookingService.getById(userFullDto1.getId(), currentBookingFullDto.getId()))
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
        assertThat(bookingService.approve(userFullDto2.getId(), currentBookingFullDto.getId(), true))
                .hasFieldOrPropertyWithValue("status", Status.APPROVED);

        assertThat(bookingService.getById(userFullDto1.getId(), futureBookingFullDto.getId()))
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
        assertThat(bookingService.approve(userFullDto2.getId(), futureBookingFullDto.getId(), false))
                .hasFieldOrPropertyWithValue("status", Status.REJECTED);
    }
}
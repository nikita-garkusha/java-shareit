package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public List<BookingFullDto> getByBookerId(Long bookerId, String subState, Integer from, Integer size) {
        State state = getState(subState);
        Pageable pageable = getPage(from, size);
        User booker = userService.getUserById(bookerId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(booker.getId(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStateCurrent(booker.getId(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStatePast(booker.getId(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStateFuture(booker.getId(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.REJECTED, pageable);
                break;
        }

        List<BookingFullDto> result = bookings
                .stream()
                .map(BookingMapper::mapToFullDto)
                .collect(Collectors.toList());

        log.info("Found {} booking(s).", result.size());
        return result;
    }

    @Override
    public List<BookingFullDto> getByOwnerId(Long ownerId, String subState, Integer from, Integer size) {
        State state = getState(subState);
        Pageable pageable = getPage(from, size);
        User owner = userService.getUserById(ownerId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(owner.getId(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(owner.getId(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndStatePast(owner.getId(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStateFuture(owner.getId(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(), Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(), Status.REJECTED, pageable);
                break;
        }

        List<BookingFullDto> result = bookings
                .stream()
                .map(BookingMapper::mapToFullDto)
                .collect(Collectors.toList());

        log.info("Found {} booking(s).", result.size());
        return result;
    }

    @SneakyThrows
    @Override
    public BookingFullDto getById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        User booker = booking.getBooker();
        User owner = userService.getUserById(booking.getItem().getOwner().getId());
        if (!booker.getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new IllegalAccessException("The booking can only be viewed " +
                    "by the author or the owner of the item.");
        }
        BookingFullDto result = bookingRepository.findById(bookingId)
                .map(BookingMapper::mapToFullDto)
                .orElseThrow();
        log.info("Booking {} is found.", result.getId());
        return result;
    }

    @SneakyThrows
    @Transactional
    @Override
    public BookingFullDto create(Long userId, BookingInputDto bookingInputDto) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingInputDto.getItemId());

        if (booker.getId().equals(item.getOwner().getId())) {
            throw new IllegalAccessException("The owner cannot book his own things");
        }

        if (!item.isAvailable()) {
            throw new IllegalStateException(String.format("Item %d is unavailable.", item.getId()));
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        BookingFullDto result =
                Optional.of(bookingRepository.save(BookingMapper.mapToBooking(bookingInputDto, booking)))
                        .map(BookingMapper::mapToFullDto)
                        .orElseThrow();
        log.info("Booking {} {} created.", result.getId(), result.getItem().getName());
        return result;
    }

    @SneakyThrows
    @Transactional
    @Override
    public BookingFullDto approve(Long userId, Long bookingId, Boolean isApproved) {
        User owner = userService.getUserById(userId);
        Booking booking = getBookingById(bookingId);
        Item item = itemService.getItemById(booking.getItem().getId());

        if (!booking.getStatus().equals(Status.WAITING) && owner.getId().equals(item.getOwner().getId())) {
            throw new IllegalStateException(String.format("Booking %d cannot be updated", bookingId));
        }
        if (!owner.getId().equals(item.getOwner().getId())) {
            throw new IllegalAccessException("Only the owner of the item can confirm the booking.");
        }

        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        log.info("Booking status changed to {}.", booking.getStatus());
        return BookingMapper.mapToFullDto(booking);
    }


    public Booking getBookingById(Long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NullPointerException(String.format("Booking %d is not found.", bookingId)));
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(state);
        }
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("start").descending());
    }
}

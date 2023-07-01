package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingInputDto bookingInputDto) {
        log.info("Получен POST-запрос к эндпоинту: '/bookings' " +
                "на создание бронирования от пользователя с ID={}", userId);
        return bookingService.create(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Получен PATCH-запрос к эндпоинту: '/bookings' на обновление статуса бронирования с ID={}", bookingId);
        return bookingService.approve(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        log.info("Получен GET-запрос к эндпоинту: '/bookings' на получение бронирования с ID={}", bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingFullDto> getByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(name = "state", defaultValue = "ALL") String subState) {
        return bookingService.getByBookerId(bookerId, subState);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String subState) {
        return bookingService.getByOwnerId(ownerId, subState);
    }
}

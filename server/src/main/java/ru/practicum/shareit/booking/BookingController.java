package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody BookingInputDto bookingInputDto) {
        return bookingService.create(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approve(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingFullDto> getByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam String state,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return bookingService.getByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam String state,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return bookingService.getByOwnerId(ownerId, state, from, size);
    }
}

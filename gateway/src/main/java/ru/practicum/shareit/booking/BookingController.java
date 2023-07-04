package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingClient.create(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam(name = "approved") Boolean isApproved) {
        return bookingClient.approve(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getByOwnerId(ownerId, state, from, size);
    }
}

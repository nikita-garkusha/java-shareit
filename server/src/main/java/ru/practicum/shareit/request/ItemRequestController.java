package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getByRequesterId(
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.getByRequesterId(requesterId);
    }


    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam Integer from,
            @RequestParam Integer size) {
        return itemRequestService.getAll(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PathVariable Long requestId) {
        return itemRequestService.getById(requesterId, requestId);
    }


    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestInputDto itemRequestInputDto) {
        return itemRequestService.create(userId, itemRequestInputDto);
    }
}

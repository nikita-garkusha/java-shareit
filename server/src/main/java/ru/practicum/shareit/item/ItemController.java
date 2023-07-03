package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemFullDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemInputDto itemInputDto) {
        return itemService.create(userId, itemInputDto);
    }

    @PatchMapping("/{itemId}")
    public ItemFullDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemInputDto itemInputDto,
                              @PathVariable Long itemId) {
        return itemService.update(userId, itemId, itemInputDto);
    }

    @GetMapping("/{itemId}")
    public ItemFullDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemFullDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam Integer from,
                                         @RequestParam Integer size) {
        return itemService.getByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemFullDto> search(@RequestParam String text,
                                    @RequestParam Integer from,
                                    @RequestParam Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentInputDto commentInputDto) {
        return itemService.addComment(userId, itemId, commentInputDto);
    }
}
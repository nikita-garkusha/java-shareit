package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

import java.util.List;

public interface ItemService {

    List<ItemFullDto> search(String text, Integer from, Integer size);

    List<ItemFullDto> getByUserId(Long userId, Integer from, Integer size);

    ItemFullDto getById(Long userId, Long itemId);

    ItemFullDto create(Long userId, ItemInputDto itemInputDto);

    ItemFullDto update(Long userId, Long itemId, ItemInputDto itemInputDto);

    Item getItemById(Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentInputDto commentInputDto);
}
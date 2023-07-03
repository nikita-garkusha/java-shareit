package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

public class ItemRequestMapper {

    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.mapToShortDto(itemRequest.getRequester()),
                itemRequest.getCreated());
    }

    public static ItemRequest mapToItemRequest(ItemRequestInputDto itemRequestInputDto, ItemRequest itemRequest) {
        itemRequest.setDescription(itemRequestInputDto.getDescription());

        return itemRequest;
    }
}

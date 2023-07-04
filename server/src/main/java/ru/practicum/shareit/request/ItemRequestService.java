package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.List;


public interface ItemRequestService {

    List<ItemRequestDto> getByRequesterId(Long requesterId);

    List<ItemRequestDto> getAll(Long requesterId, Integer from, Integer size);

    ItemRequestDto create(Long userId, ItemRequestInputDto itemRequestInputDto);

    ItemRequestDto getById(Long requesterId, Long requestId);

    ItemRequest getRequestById(Long requestId);
}

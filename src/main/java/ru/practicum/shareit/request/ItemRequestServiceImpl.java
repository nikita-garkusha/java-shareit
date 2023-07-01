package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public List<ItemRequestDto> getByRequesterId(Long requesterId) {
        User requester = userService.getUserById(requesterId);
        List<ItemRequestDto> result = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requester.getId())
                .stream()
                .map(ItemRequestMapper::mapToDto)
                .collect(Collectors.toList());

        result.forEach(itemRequestDto -> itemRequestDto.setItems(itemRepository
                .findAllByItemRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::mapToShortDto)
                .collect(Collectors.toList())));
        log.info("Found {} booking(s).", result.size());
        return result;
    }

    @Override
    public List<ItemRequestDto> getAll(Long requesterId, Integer from, Integer size) {
        User requester = userService.getUserById(requesterId);
        Pageable pageable = getPage(from, size);
        List<ItemRequestDto> result = itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(requester.getId(), pageable)
                .stream()
                .map(ItemRequestMapper::mapToDto)
                .collect(Collectors.toList());

        result.forEach(itemRequestDto -> itemRequestDto.setItems(itemRepository
                .findAllByItemRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::mapToShortDto)
                .collect(Collectors.toList())));
        log.info("Found {} booking(s).", result.size());
        return result;
    }

    public ItemRequestDto create(Long userId, ItemRequestInputDto itemRequestInputDto) {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto result = Optional.of(itemRequestRepository
                        .save(ItemRequestMapper.mapToItemRequest(itemRequestInputDto, itemRequest)))
                .map(ItemRequestMapper::mapToDto)
                .orElseThrow();
        log.info("Request {} created.", result.getId());
        return result;
    }

    @SneakyThrows
    @Override
    public ItemRequestDto getById(Long requesterId, Long requestId) {
        User requester = userService.getUserById(requesterId);
        ItemRequest itemRequest = getRequestById(requestId);

        ItemRequestDto result = itemRequestRepository.findById(itemRequest.getId())
                .map(ItemRequestMapper::mapToDto)
                .orElseThrow();

        result.setItems(itemRepository
                .findAllByItemRequestId(result.getId())
                .stream()
                .map(ItemMapper::mapToShortDto)
                .collect(Collectors.toList()));
        log.info("Request {} is found.", result.getId());
        return result;
    }

    public ItemRequest getRequestById(Long requestId) {
        ItemRequest result = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NullPointerException(String.format("Request %d is not found.", requestId)));
        log.info("Request {} is found.", result.getId());
        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("created").descending());
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentInputDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public
class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Override
    public List<ItemFullDto> search(String text, Integer from, Integer size) {
        if (text.length() == 0) {
            log.debug("Search by empty text.");
            return Collections.emptyList();
        }
        Pageable pageable = getPage(from, size);
        List<ItemFullDto> result = itemRepository
                .search(text, pageable).stream()
                .map(item -> addData(-1L, item))
                .collect(Collectors.toList());
        log.info("Found {} item(s).", result.size());
        return result;
    }

    @Override
    public List<ItemFullDto> getByUserId(Long userId, Integer from, Integer size) {
        User user = userService.getUserById(userId);
        Pageable pageable = getPage(from, size);
        List<ItemFullDto> result = itemRepository.findAllByOwnerId(user.getId(), pageable).stream()
                .map(item -> addData(user.getId(), item))
                .collect(Collectors.toList());
        log.info("Found {} item(s).", result.size());
        return result;
    }

    @Override
    public ItemFullDto getById(Long userId, Long itemId) {
        ItemFullDto result = itemRepository
                .findById(itemId)
                .map(item -> addData(userId, item))
                .orElseThrow(() -> new NullPointerException(String.format("Item %d is not found.", itemId)));
        log.info("User {} is found.", result.getId());
        return result;
    }

    @Override
    public ItemFullDto create(Long userId, ItemInputDto itemInputDto) {
        User user = userService.getUserById(userId);
        Item newItem = new Item();
        newItem.setOwner(user);

        if (itemInputDto.getRequestId() != null) {
            newItem.setItemRequest(itemRequestService.getRequestById(itemInputDto.getRequestId()));
        }

        ItemFullDto result = Optional.of(itemRepository.save(ItemMapper.mapToItem(itemInputDto, newItem)))
                .map(item -> addData(userId, item))
                .orElseThrow();
        log.info("Item {} {} created.", result.getId(), result.getName());
        return result;
    }

    @Override
    public ItemFullDto update(Long userId, Long itemId, ItemInputDto itemInputDto) {
        User user = userService.getUserById(userId);
        Item oldItem = getItemById(itemId);
        if (!user.getId().equals(oldItem.getOwner().getId())) {
            log.warn("User {} is not the owner of the item {}.", userId, oldItem.getId());
            throw new IllegalArgumentException("Only the owner can edit an item");
        }
        ItemFullDto result = Optional.of(itemRepository.save(ItemMapper.mapToItem(itemInputDto, oldItem)))
                .map(item -> addData(userId, item))
                .orElseThrow();
        log.info("Item {} {} updated.", result.getId(), result.getName());
        return result;
    }

    public Item getItemById(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NullPointerException(String.format("Item %d is not found.", itemId)));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentInputDto commentInputDto) {
        User author = userService.getUserById(userId);
        Item item = getItemById(itemId);

        if (!bookingRepository
                .existsByBookerIdAndItemIdAndEndBefore(author.getId(), item.getId(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            throw new NoSuchElementException("The user has not booked this item.");
        }

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(author);

        CommentDto commentDto =
                Optional.of(commentRepository.save(CommentMapper.mapToComment(commentInputDto, comment)))
                        .map(CommentMapper::mapToDto)
                        .orElseThrow();
        log.info("Comment {} added to item {}.", commentDto.getId(), item.getId());
        return commentDto;
    }

    public ItemFullDto addData(Long userId, Item item) {
        ItemFullDto result = ItemMapper.mapToFullDto(item);

        if (result.getOwner().getId().equals(userId)) {
            result.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(result.getId(),
                            LocalDateTime.now(),
                            Status.APPROVED)
                    .map(BookingMapper::mapToShortDto)
                    .orElse(null));

            result.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByEndAsc(result.getId(),
                            LocalDateTime.now(),
                            Status.APPROVED)
                    .map(BookingMapper::mapToShortDto)
                    .orElse(null));
        }

        result.setComments(commentRepository.findAllByItemId(result.getId())
                .stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList()));

        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
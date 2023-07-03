package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;


    private User user1;
    private ItemRequest itemRequest1;
    private ItemRequestInputDto itemRequestInputDto1;
    private ItemRequestDto itemRequestDto1;
    private Item item1;

    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "sadsa", "dsads@dsads.ru");
        itemRequest1 = new ItemRequest(1L, "dsadsa", user1,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        item1 = new Item(1L, "sdad", "dswads", true, user1, itemRequest1);
        itemRequestInputDto1 = new ItemRequestInputDto(itemRequest1.getDescription());
        itemRequestDto1 = ItemRequestMapper.mapToDto(itemRequest1);
        itemRequestDto1.setItems(List.of(ItemMapper.mapToShortDto(item1)));
        pageable = PageRequest.of(1 / 20, 20, Sort.by("created").descending());
    }


    @Test
    void getByRequesterId_return1ItemRequest_added1ItemRequest() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(user1.getId())).thenReturn(List.of(itemRequest1));
        when(itemRepository.findAllByItemRequestId(itemRequestDto1.getId())).thenReturn(List.of(item1));

        assertThat(itemRequestService.getByRequesterId(user1.getId())).asList().contains(itemRequestDto1);
    }

    @Test
    void getAll_return1ItemRequest_added1ItemRequest() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(user1.getId(), pageable)).thenReturn(List.of(itemRequest1));
        when(itemRepository.findAllByItemRequestId(itemRequestDto1.getId())).thenReturn(List.of(item1));

        assertThat(itemRequestService.getAll(user1.getId(), 1, 20)).asList().contains(itemRequestDto1);
    }

    @Test
    void create_return1ItemRequest_added1ItemRequest() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest1);
        itemRequestDto1.setItems(null);

        assertThat(itemRequestService.create(user1.getId(), itemRequestInputDto1)).isEqualTo(itemRequestDto1);
    }

    @Test
    void getById_return1ItemRequest_added1ItemRequest() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestRepository.findById(itemRequest1.getId())).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByItemRequestId(itemRequestDto1.getId())).thenReturn(List.of(item1));

        assertThat(itemRequestService.getById(user1.getId(), itemRequest1.getId())).isEqualTo(itemRequestDto1);
    }

    @Test
    void getRequestById_return1ItemRequest_added1ItemRequest() {
        when(itemRequestRepository.findById(itemRequest1.getId())).thenReturn(Optional.of(itemRequest1));

        assertThat(itemRequestService.getRequestById(itemRequest1.getId())).isEqualTo(itemRequest1);
    }
}
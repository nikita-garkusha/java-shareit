package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;


    private User user1;
    private Item item1;
    private ItemRequest itemRequest1;
    private ItemFullDto itemFullDto1;
    private ItemInputDto itemInputDto1;
    private Comment comment;
    private CommentInputDto commentInputDto;
    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "sadsa", "dsads@dsads.ru");
        User user2 = new User(2L, "sadsadsa", "dsasdadsds@ddsadssads.ru");
        itemRequest1 = new ItemRequest(1L, "dswads", user2,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        item1 = new Item(1L, "sdad", "dswads", true, user1, itemRequest1);
        itemFullDto1 = ItemMapper.mapToFullDto(item1);
        itemInputDto1 = new ItemInputDto(item1.getId(),
                item1.getName(),
                item1.getDescription(),
                item1.isAvailable(),
                itemRequest1.getId());
        comment = new Comment(1L, "dfseadsa", item1, user1,
                LocalDateTime.now());
        commentInputDto = new CommentInputDto(comment.getText());
        pageable = PageRequest.of(1 / 20, 20, Sort.by("id").ascending());
        itemFullDto1.setComments(new ArrayList<>());
    }

    @Test
    void search_return1Item_withText() {
        when(itemRepository.search("sdad", pageable)).thenReturn(List.of(item1));

        assertThat(itemService.search("sdad", 1, 20)).asList().contains(itemFullDto1);
    }

    @Test
    void search_returnEmpty_wrongText() {
        assertThat(itemService.search("", 1, 20).size()).isEqualTo(0);
        verify(itemRepository, times(0)).search("", pageable);
    }

    @Test
    void search_throwIllegalArgumentException_wrongSizeOrFrom() {
        assertThrows(IllegalArgumentException.class, () -> itemService.search("asdad", 0, 0));
    }

    @Test
    void getByUserId_return1Item_add2Item() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRepository.findAllByOwnerId(1L, pageable)).thenReturn(List.of(item1));

        assertThat(itemService.getByUserId(1L, 1, 20)).asList().contains(itemFullDto1);
    }

    @Test
    void getByUserId_throwNullPointerException_add2Items() {
        assertThrows(NullPointerException.class, () -> itemService.getByUserId(999L, 1, 20));
    }

    @Test
    void getById_returnItem1_add2Items() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        itemFullDto1.setComments(new ArrayList<>());
        assertThat(itemService.getById(1L, 1L)).isEqualTo(itemFullDto1);
    }

    @Test
    void getById_throwNullPointerException_add2Items() {
        assertThrows(NullPointerException.class, () -> itemService.getById(1L, 999L));
    }

    @Test
    void create_returnItem_addItem() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRequestService.getRequestById(itemInputDto1.getRequestId())).thenReturn(itemRequest1);
        when(itemRepository.save(item1)).thenReturn(item1);

        assertThat(itemService.create(1L, itemInputDto1)).isEqualTo(itemFullDto1);
    }

    @Test
    void update_returnItem_updateItem() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1)).thenReturn(item1);

        assertThat(itemService.update(1L, 1L, itemInputDto1)).isEqualTo(itemFullDto1);
    }

    @Test
    void getItemById_returnItem_addItem() {
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        assertThat(itemService.getItemById(item1.getId())).isEqualTo(item1);
    }

    @Test
    void getItemById_throwNullPointerException_addItem() {
        assertThrows(NullPointerException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void addComment() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(1L, 1L,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(1L, 1L, commentInputDto);

        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(commentDto.getCreated().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(comment.getCreated().truncatedTo(ChronoUnit.SECONDS));
    }
}
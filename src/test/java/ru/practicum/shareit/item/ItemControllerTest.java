package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemServiceMock;

    private ItemInputDto itemInputDto1;
    private ItemFullDto itemFullDto1;
    private ItemFullDto itemFullDto2;
    private CommentDto commentDto;
    private CommentInputDto commentInputDto;

    @BeforeEach
    void beforeEach() {
        itemInputDto1 = new ItemInputDto();
        itemInputDto1.setId(1L);
        itemInputDto1.setName("fdsadsa");
        itemInputDto1.setDescription("132132sadsadsa");
        itemInputDto1.setAvailable(true);
        Item item1 = new Item(1L, "wqewq", "dsadsa", true,
                new User(1L, "fdsfds", "fdesfds"),
                new ItemRequest(1L, "wqewqew",
                        new User(2L, "ewqewq", "21321sadsa"),
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        UserShortDto userShortDto1 = new UserShortDto(1L, "");
        itemFullDto1 = new ItemFullDto();
        itemFullDto1.setOwner(userShortDto1);
        itemFullDto1 = ItemMapper.mapToFullDto(ItemMapper.mapToItem(itemInputDto1, item1));

        ItemInputDto itemInputDto2 = new ItemInputDto();
        itemInputDto2.setId(2L);
        itemInputDto2.setName("fdsadsa");
        itemInputDto2.setDescription("132132sadsadsa");
        itemInputDto2.setAvailable(true);
        Item item2 = new Item(2L, "wqewq", "dsadsa", true,
                new User(1L, "fdsfds", "fdesfds"),
                new ItemRequest(2L, "wqewqew",
                        new User(2L, "ewqewq", "21321sadsa@sdsa.ru"),
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        UserShortDto userShortDto2 = new UserShortDto(1L, "");
        itemFullDto2 = new ItemFullDto();
        itemFullDto2.setOwner(userShortDto2);
        itemFullDto2 = ItemMapper.mapToFullDto(ItemMapper.mapToItem(itemInputDto1, item2));

        commentDto = new CommentDto(1L,
                "sadsadsa",
                "dfsadsa",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentInputDto = new CommentInputDto(commentDto.getText());
    }

    @SneakyThrows
    @Test
    void create_returnItem_addedItem() {
        when(itemServiceMock.create(1L, itemInputDto1)).thenReturn(itemFullDto1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemInputDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemFullDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemFullDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemFullDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemFullDto1.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(itemFullDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.name").value(itemFullDto1.getOwner().getName()))
                .andExpect(jsonPath("$.requestId").value(itemFullDto1.getRequestId()));
        verify(itemServiceMock).create(1L, itemInputDto1);
    }

    @SneakyThrows
    @Test
    void get_returnItem_addedItem() {
        when(itemServiceMock.getById(1L, 1L)).thenReturn(itemFullDto1);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemFullDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemFullDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemFullDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemFullDto1.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(itemFullDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.name").value(itemFullDto1.getOwner().getName()))
                .andExpect(jsonPath("$.requestId").value(itemFullDto1.getRequestId()));
        verify(itemServiceMock).getById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void update_returnItem_addedItem() {
        when(itemServiceMock.update(1L, 1L, itemInputDto1)).thenReturn(itemFullDto1);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemInputDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemFullDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemFullDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemFullDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemFullDto1.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(itemFullDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.name").value(itemFullDto1.getOwner().getName()))
                .andExpect(jsonPath("$.requestId").value(itemFullDto1.getRequestId()));
        verify(itemServiceMock).update(1L, 1L, itemInputDto1);
    }

    @SneakyThrows
    @Test
    void getByUserId_return2ItemByUser1_added2Item() {
        when(itemServiceMock.getByUserId(1L, 1, 20))
                .thenReturn(List.of(itemFullDto1, itemFullDto2));

        mockMvc.perform(get("/items", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemFullDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemFullDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemFullDto1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemFullDto1.getAvailable()))
                .andExpect(jsonPath("$[0].owner.id").value(itemFullDto1.getOwner().getId()))
                .andExpect(jsonPath("$[0].owner.name").value(itemFullDto1.getOwner().getName()))
                .andExpect(jsonPath("$[0].requestId").value(itemFullDto1.getRequestId()))
                .andExpect(jsonPath("$[1].id").value(itemFullDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(itemFullDto2.getName()))
                .andExpect(jsonPath("$[1].description").value(itemFullDto2.getDescription()))
                .andExpect(jsonPath("$[1].available").value(itemFullDto2.getAvailable()))
                .andExpect(jsonPath("$[1].owner.id").value(itemFullDto2.getOwner().getId()))
                .andExpect(jsonPath("$[1].owner.name").value(itemFullDto2.getOwner().getName()))
                .andExpect(jsonPath("$[1].requestId").value(itemFullDto2.getRequestId()));
        verify(itemServiceMock).getByUserId(1L, 1, 20);
    }

    @SneakyThrows
    @Test
    void search_returnItem_addItem() {
        when(itemServiceMock.search("fdsadsa", 1, 20)).thenReturn(List.of(itemFullDto1));

        mockMvc.perform(get("/items/search")
                        .param("text", "fdsadsa")
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemFullDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemFullDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemFullDto1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemFullDto1.getAvailable()))
                .andExpect(jsonPath("$[0].owner.id").value(itemFullDto1.getOwner().getId()))
                .andExpect(jsonPath("$[0].owner.name").value(itemFullDto1.getOwner().getName()))
                .andExpect(jsonPath("$[0].requestId").value(itemFullDto1.getRequestId()));
        verify(itemServiceMock).search("fdsadsa", 1, 20);
    }

    @SneakyThrows
    @Test
    void addComment_returnComment_addComment() {
        when(itemServiceMock.addComment(1L, 1L, commentInputDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentInputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString()));
        verify(itemServiceMock).addComment(1L, 1L, commentInputDto);
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIT {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService itemRequestService;

    private UserFullDto userFullDto1;
    private UserFullDto userFullDto2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;
    ItemShortDto itemShortDto1;
    ItemShortDto itemShortDto2;
    ItemInputDto itemInputDto1;

    @BeforeEach
    void beforeEach() {
        UserInputDto userInputDto1 = new UserInputDto(null, "swadsa", "dsadsa");
        UserInputDto userInputDto2 = new UserInputDto(null, "swsadadsa", "dssadadsa");
        userFullDto1 = userService.create(userInputDto1);
        userFullDto2 = userService.create(userInputDto2);
        ItemRequestInputDto itemRequestInputDto1 = new ItemRequestInputDto("sadsadsa");
        ItemRequestInputDto itemRequestInputDto2 = new ItemRequestInputDto("sadsadsadsasa");
        itemRequestDto1 = itemRequestService.create(userFullDto1.getId(), itemRequestInputDto1);
        itemRequestDto2 = itemRequestService.create(userFullDto2.getId(), itemRequestInputDto2);
        itemInputDto1 =
                new ItemInputDto(null,
                        "asdfgh",
                        "asdfghdfgh",
                        true,
                        itemRequestDto2.getId());

        ItemInputDto itemInputDto2 =
                new ItemInputDto(null,
                        "asdfghjk",
                        "zxcvbnmjk",
                        true,
                        itemRequestDto1.getId());
        ItemFullDto itemFullDto1 = itemService.create(userFullDto2.getId(), itemInputDto1);
        ItemFullDto itemFullDto2 = itemService.create(userFullDto1.getId(), itemInputDto2);
        itemShortDto1 = ItemMapper.mapToShortDto(itemService.getItemById(itemFullDto1.getId()));
        itemShortDto2 = ItemMapper.mapToShortDto(itemService.getItemById(itemFullDto2.getId()));

        itemRequestDto1.setItems(itemRequestService.getById(itemRequestDto1.getRequester().getId(),
                itemRequestDto1.getId()).getItems());
        itemRequestDto2.setItems(itemRequestService.getById(itemRequestDto2.getRequester().getId(),
                itemRequestDto2.getId()).getItems());
    }

    @Test
    void create_findItemRequest_added3ItemRequests() {
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto("sadsadsa");
        ItemRequestDto itemRequestDto = itemRequestService.create(userFullDto1.getId(), itemRequestInputDto);

        itemRequestDto.setItems(itemRequestService.getById(itemRequestDto.getRequester().getId(),
                itemRequestDto.getId()).getItems());

        assertThat(itemRequestService.getById(userFullDto2.getId(), itemRequestDto.getId())).isEqualTo(itemRequestDto);
    }


    @Test
    void getByRequesterId_return1ItemRequestEveryTime_added2ItemRequests() {
        List<ItemRequestDto> result1 = itemRequestService.getByRequesterId(userFullDto1.getId());
        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.get(0)).isEqualTo(itemRequestDto1);

        List<ItemRequestDto> result2 = itemRequestService.getByRequesterId(userFullDto2.getId());
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.get(0)).isEqualTo(itemRequestDto2);
    }

    @Test
    void getAll_return1ItemRequestEveryTime_added2ItemRequests() {
        List<ItemRequestDto> result1 = itemRequestService.getAll(userFullDto1.getId(), 1, 20);
        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.get(0)).isEqualTo(itemRequestDto2);

        List<ItemRequestDto> result2 = itemRequestService.getAll(userFullDto2.getId(), 1, 20);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.get(0)).isEqualTo(itemRequestDto1);
    }

    @Test
    void getById_return1ItemRequestEveryTime_added2ItemRequests() {
        assertThat(itemRequestService.getById(userFullDto2.getId(), itemRequestDto1.getId()))
                .isEqualTo(itemRequestDto1);

        assertThat(itemRequestService.getById(userFullDto1.getId(), itemRequestDto2.getId()))
                .isEqualTo(itemRequestDto2);
    }
}
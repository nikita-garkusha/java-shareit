package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIT {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private UserFullDto userFullDto1;
    private UserFullDto userFullDto2;
    private ItemFullDto itemFullDto1;
    private ItemFullDto itemFullDto2;

    @BeforeEach
    void beforeEach() {
        UserInputDto userInputDto1 = new UserInputDto(null, "swadsa", "dsadsa");
        UserInputDto userInputDto2 = new UserInputDto(null, "swsadadsa", "dssadadsa");
        userFullDto1 = userService.create(userInputDto1);
        userFullDto2 = userService.create(userInputDto2);

        ItemInputDto itemInputDto1 =
                new ItemInputDto(null, "asdfgh", "asdfghdfgh", true, null);
        ItemInputDto itemInputDto2 =
                new ItemInputDto(null, "asdfghjk", "zxcvbnmjk", true, null);

        itemFullDto1 = itemService.create(userFullDto1.getId(), itemInputDto1);
        itemFullDto2 = itemService.create(userFullDto2.getId(), itemInputDto2);
        itemFullDto1.setComments(new ArrayList<>());
        itemFullDto2.setComments(new ArrayList<>());
    }

    @Test
    void create_findItem_added3Items() {
        ItemInputDto itemInputDto =
                new ItemInputDto(null, "asdfgh", "asdfghdfgh", true, null);
        ItemFullDto itemFullDto = itemService.create(userFullDto1.getId(), itemInputDto);

        assertThat(itemService.getById(userFullDto1.getId(), itemFullDto.getId())).isEqualTo(itemFullDto);
    }


    @Test
    void search_return4Results_added2Items() {
        List<ItemFullDto> result1 = itemService.search("asdfgh", 1, 20);
        List<ItemFullDto> result2 = itemService.search("jk", 1, 20);
        List<ItemFullDto> result3 = itemService.search("asdfghdfgh", 1, 20);
        List<ItemFullDto> result4 = itemService.search("assdadsadsadsadsadfghdfgh", 1, 20);

        assertThat(result1).asList().containsExactly(itemFullDto1, itemFullDto2);
        assertThat(result2).asList().containsExactly(itemFullDto2);
        assertThat(result3).asList().containsExactly(itemFullDto1);
        assertThat(result4.size()).isEqualTo(0);
    }

    @Test
    void getByUserId_return1ItemEveryTime_added2Items() {
        assertThat(itemService.getByUserId(userFullDto1.getId(), 0, 20).get(0)).isEqualTo(itemFullDto1);
        assertThat(itemService.getByUserId(userFullDto2.getId(), 0, 20).get(0)).isEqualTo(itemFullDto2);
    }

    @Test
    void getById_return2Items_added2Items() {
        assertThat(itemService.getById(userFullDto1.getId(), itemFullDto1.getId())).isEqualTo(itemFullDto1);
        assertThat(itemService.getById(userFullDto2.getId(), itemFullDto2.getId())).isEqualTo(itemFullDto2);
    }
}
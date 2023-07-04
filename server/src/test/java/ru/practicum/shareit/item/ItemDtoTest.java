package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemFullDto> itemFullDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemShortDto> itemShortDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemInputDto> itemInputDtoJacksonTester;

    @Test
    void itemFullDtoSerializationTest() throws IOException {
        User user = new User();
        user.setId(2L);
        user.setName("Dmitriy");
        user.setEmail("dm@yandex.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна вундервафля");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Item item = new Item();
        item.setId(1L);
        item.setName("Вундервафля");
        item.setDescription("Чудо инженерной мысли");
        item.setAvailable(true);
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        ItemFullDto itemFullDto = ItemMapper.mapToFullDto(item);
        JsonContent<ItemFullDto> itemFullDtoJsonContent = itemFullDtoJacksonTester.write(itemFullDto);

        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(item.getId()));
        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(item.isAvailable());
        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(Math.toIntExact(item.getItemRequest().getId()));

        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.owner.id")
                .isEqualTo(Math.toIntExact(item.getOwner().getId()));
        assertThat(itemFullDtoJsonContent)
                .extractingJsonPathStringValue("$.owner.name").isEqualTo(item.getOwner().getName());
    }

    @Test
    void itemShortDtoSerializationTest() throws IOException {
        User user = new User();
        user.setId(2L);
        user.setName("Dmitriy");
        user.setEmail("dm@yandex.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна вундервафля");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Item item = new Item();
        item.setId(1L);
        item.setName("Вундервафля");
        item.setDescription("Чудо инженерной мысли");
        item.setAvailable(true);
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        ItemShortDto itemShortDto = ItemMapper.mapToShortDto(item);
        JsonContent<ItemShortDto> itemShortDtoJsonContent = itemShortDtoJacksonTester.write(itemShortDto);

        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(item.getId()));
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(item.isAvailable());
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(Math.toIntExact(item.getItemRequest().getId()));
    }

    @Test
    void userInputDtoDeserializationTest() throws IOException {
        ItemInputDto itemInputDto =
                new ItemInputDto(2L, "Палка", "Просто палка", true, 6L);

        JsonContent<ItemInputDto> itemInputDtoJsonContent = itemInputDtoJacksonTester.write(itemInputDto);
        Item newItem = ItemMapper.mapToItem(itemInputDtoJacksonTester.parseObject(
                itemInputDtoJsonContent.getJson()), new Item());

        assertThat(newItem).hasFieldOrPropertyWithValue("id", itemInputDto.getId());
        assertThat(newItem).hasFieldOrPropertyWithValue("name", itemInputDto.getName());
        assertThat(newItem).hasFieldOrPropertyWithValue("description", itemInputDto.getDescription());
        assertThat(newItem).hasFieldOrPropertyWithValue("available", itemInputDto.getAvailable());
        assertThat(itemInputDtoJsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(Math.toIntExact(itemInputDto.getRequestId()));
    }
}

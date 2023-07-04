package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemRequestInputDto> itemRequestInputDtoJacksonTester;

    @Test
    void itemRequestDtoSerializationTest() throws IOException {
        User user = new User();
        user.setId(1L);
        user.setName("Viktor");
        user.setEmail("Ivanich@gmail.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужна вундервафля");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToDto(itemRequest);
        JsonContent<ItemRequestDto> itemRequestDtoJsonContent = itemRequestDtoJacksonTester.write(itemRequestDto);

        assertThat(itemRequestDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(itemRequest.getId()));
        assertThat(itemRequestDtoJsonContent)
                .extractingJsonPathStringValue("$.description").isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDtoJsonContent)
                .extractingJsonPathStringValue("$.created").isEqualTo(itemRequest.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    void itemRequestInputDtoDeserializationTest() throws IOException {
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto("Шо-то у Ашота");

        JsonContent<ItemRequestInputDto> itemRequestInputDtoJsonContent = itemRequestInputDtoJacksonTester
                .write(itemRequestInputDto);
        ItemRequest newItemRequest = ItemRequestMapper.mapToItemRequest(itemRequestInputDtoJacksonTester
                .parseObject(itemRequestInputDtoJsonContent.getJson()), new ItemRequest());

        assertThat(newItemRequest).hasFieldOrPropertyWithValue("description",
                itemRequestInputDto.getDescription());
    }
}

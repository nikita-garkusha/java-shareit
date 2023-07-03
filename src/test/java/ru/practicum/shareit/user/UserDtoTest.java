package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserFullDto> userFullDtoJacksonTester;
    @Autowired
    private JacksonTester<UserShortDto> userShortDtoJacksonTester;
    @Autowired
    private JacksonTester<UserInputDto> userInputDtoJacksonTester;

    @Test
    void userFullDtoSerializationTest() throws IOException {
        User user = new User();
        user.setId(1L);
        user.setName("Viktor");
        user.setEmail("Ivanich@gmail.com");

        UserFullDto userFullDto = UserMapper.mapToFullDto(user);
        JsonContent<UserFullDto> userFullDtoJsonContent = userFullDtoJacksonTester.write(userFullDto);

        assertThat(userFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(user.getId()));
        assertThat(userFullDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(userFullDtoJsonContent)
                .extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
    }

    @Test
    void userShortDtoSerializationTest() throws IOException {
        User user = new User();
        user.setId(1L);
        user.setName("Viktor");
        user.setEmail("Ivanich@gmail.com");


        UserShortDto userShortDto = UserMapper.mapToShortDto(user);
        JsonContent<UserShortDto> userShortDtoJsonContent = userShortDtoJacksonTester.write(userShortDto);

        assertThat(userShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(user.getId()));
        assertThat(userShortDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
    }

    @Test
    void userInputDtoDeserializationTest() throws IOException {
        UserInputDto userInputDto = new UserInputDto(2L, "Dmitriy", "dm@yandex.ru");

        JsonContent<UserInputDto> userInputDtoJsonContent = userInputDtoJacksonTester.write(userInputDto);
        User newUser = UserMapper
                .mapToUser(userInputDtoJacksonTester.parseObject(userInputDtoJsonContent.getJson()), new User());

        assertThat(newUser).hasFieldOrPropertyWithValue("id", userInputDto.getId());
        assertThat(newUser).hasFieldOrPropertyWithValue("name", userInputDto.getName());
        assertThat(newUser).hasFieldOrPropertyWithValue("email", userInputDto.getEmail());
    }
}
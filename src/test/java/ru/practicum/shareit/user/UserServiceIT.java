package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Test
    void getAll_return2Users_added2Users() {
        UserInputDto userInputDto1 = new UserInputDto(null, "swadsa", "dsadsa");
        UserInputDto userInputDto2 = new UserInputDto(null, "swsadadsa", "dssadadsa");
        UserFullDto userFullDto1 = userService.create(userInputDto1);
        UserFullDto userFullDto2 = userService.create(userInputDto2);

        List<UserFullDto> users = userService.getAll();

        assertThat(users.size()).isEqualTo(2);

        assertThat(users.get(0)).isEqualTo(userFullDto1);

        assertThat(users.get(1)).isEqualTo(userFullDto2);
    }
}
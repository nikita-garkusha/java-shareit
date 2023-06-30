package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    public static final int MIN_ID_VALUE = 1;
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Received request to create user: {}", userDto.getName());

        User user = mapper.toModel(userDto, null);
        User createdUser = userService.createUser(user);

        logger.info("Created user: {}", createdUser.getName());

        return mapper.toDto(createdUser);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@NotNull(message = NULL_USER_ID_MESSAGE)
                                @Min(MIN_ID_VALUE)
                                @PathVariable Long userId) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Received request to find user with id: {}", userId);

        User foundUser = userService.findUserById(userId);

        logger.info("Found user: {}", foundUser.getName());

        return mapper.toDto(foundUser);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Received request to find all users");

        List<User> users = userService.findAllUsers();

        logger.info("Found users: {}", users);

        return mapper.mapUserListToUserDtoList(users);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull(message = NULL_USER_ID_MESSAGE)
                              @Min(MIN_ID_VALUE)
                              @PathVariable Long userId,
                              @Validated({Update.class})
                              @RequestBody UserDto userDto) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Received request to update user id {} with data: {}", userId, userDto.getName());

        User user = mapper.toModel(userDto, userId);
        User updatedUser = userService.updateUser(userId, user);

        logger.info("Updated user: {}", updatedUser.getName());

        return mapper.toDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull(message = NULL_USER_ID_MESSAGE)
                               @Min(MIN_ID_VALUE)
                               @PathVariable Long userId) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Received request to delete user with id: {}", userId);

        userService.deleteUserById(userId);

        logger.info("Deleted user with id: {}", userId);
    }
}

package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toModel(UserDto userDto, Long userId) {
        return User.builder()
                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public List<UserDto> mapUserListToUserDtoList(List<User> users) {
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

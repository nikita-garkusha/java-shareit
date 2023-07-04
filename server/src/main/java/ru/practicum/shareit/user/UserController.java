package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserFullDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserFullDto getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public UserFullDto create(@RequestBody UserInputDto userInputDto) {
        return userService.create(userInputDto);
    }

    @PatchMapping("/{userId}")
    public UserFullDto update(@RequestBody UserInputDto userInputDto, @PathVariable Long userId) {
        return userService.update(userInputDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}

package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserInputDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
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
    public UserFullDto create(@Valid @RequestBody UserInputDto userInputDto) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя");
        return userService.create(userInputDto);
    }

    @PatchMapping("/{userId}")
    public UserFullDto update(@RequestBody UserInputDto userInputDto, @PathVariable Long userId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", userId);
        return userService.update(userInputDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с ID={}", userId);
        userService.deleteById(userId);
    }
}

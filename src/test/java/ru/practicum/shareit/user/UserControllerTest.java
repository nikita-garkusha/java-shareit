package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userServiceMock;

    private UserInputDto userInputDto1;
    private UserInputDto userInputDto2;

    @BeforeEach
    void beforeEach() {
        userInputDto1 = new UserInputDto();
        userInputDto1.setId(1L);
        userInputDto1.setName("viktor");
        userInputDto1.setEmail("dsfad@yandex.ru");

        userInputDto2 = new UserInputDto();
        userInputDto2.setId(2L);
        userInputDto2.setName("dsafdas");
        userInputDto2.setEmail("dfsafdsafda@yandex.ru");
    }

    @SneakyThrows
    @Test
    void getAll_return2User_add2User() {
        when(userServiceMock.getAll())
                .thenReturn(List.of(UserMapper.mapToFullDto(UserMapper.mapToUser(userInputDto1, new User())),
                        UserMapper.mapToFullDto(UserMapper.mapToUser(userInputDto2, new User()))));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userInputDto1.getId()))
                .andExpect(jsonPath("$[0].email").value(userInputDto1.getEmail()))
                .andExpect(jsonPath("$[0].name").value(userInputDto1.getName()))
                .andExpect(jsonPath("$[1].id").value(userInputDto2.getId()))
                .andExpect(jsonPath("$[1].email").value(userInputDto2.getEmail()))
                .andExpect(jsonPath("$[1].name").value(userInputDto2.getName()));
        verify(userServiceMock).getAll();
    }

    @SneakyThrows
    @Test
    void getById_returnNewUser_addUser() {
        when(userServiceMock.getById(1L))
                .thenReturn(UserMapper.mapToFullDto(UserMapper.mapToUser(userInputDto1, new User())));

        mockMvc.perform(get("/users/{id}", userInputDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userInputDto1.getId()))
                .andExpect(jsonPath("$.email").value(userInputDto1.getEmail()))
                .andExpect(jsonPath("$.name").value(userInputDto1.getName()));
        verify(userServiceMock).getById(userInputDto1.getId());
    }

    @SneakyThrows
    @Test
    void getById_UserNotFound_returnNullPointerException() {
        when(userServiceMock.getById(1L)).thenThrow(NullPointerException.class);

        mockMvc.perform(get("/users/{id}", userInputDto1.getId())).andExpect(status().isNotFound());
        verify(userServiceMock).getById(1L);
    }

    @SneakyThrows
    @Test
    void create_returnNewUser_addUser() {
        when(userServiceMock.create(userInputDto1))
                .thenReturn(UserMapper.mapToFullDto(UserMapper.mapToUser(userInputDto1, new User())));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userInputDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userInputDto1.getId()))
                .andExpect(jsonPath("$.name").value(userInputDto1.getName()))
                .andExpect(jsonPath("$.email").value(userInputDto1.getEmail()));
        verify(userServiceMock).create(userInputDto1);
    }

    @SneakyThrows
    @Test
    void update_returnNewUser_addUpdateUser() {
        when(userServiceMock.update(userInputDto1, userInputDto1.getId()))
                .thenReturn(UserMapper.mapToFullDto(UserMapper.mapToUser(userInputDto1, new User())));

        mockMvc.perform(patch("/users/{id}", userInputDto1.getId())
                        .content(objectMapper.writeValueAsString(userInputDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userInputDto1.getId()))
                .andExpect(jsonPath("$.name").value(userInputDto1.getName()))
                .andExpect(jsonPath("$.email").value(userInputDto1.getEmail()));
        verify(userServiceMock).update(userInputDto1, userInputDto1.getId());
    }

    @SneakyThrows
    @Test
    void deleteById() {
        mockMvc.perform(delete("/users/{id}", userInputDto1.getId()))
                .andExpect(status().isOk());
        verify(userServiceMock).deleteById(userInputDto1.getId());
    }

}

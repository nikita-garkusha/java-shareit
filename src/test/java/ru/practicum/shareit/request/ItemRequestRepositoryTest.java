package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User userFromDb;
    private ItemRequest itemRequestFromDb;

    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        user.setName("vityok");
        user.setEmail("vityok@mail.com");
        userFromDb = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен кирпич");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));
        itemRequestFromDb = itemRequestRepository.save(itemRequest);

        pageable = PageRequest.of(1 / 20, 20, Sort.by("id").descending());
    }

    @Test
    void findAllByRequesterId_return1ItemRequest_added1Request() {
        assertEquals(List.of(itemRequestFromDb),
                itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userFromDb.getId()));
    }

    @Test
    void findAllByRequesterId_returnEmpty_added1Request() {
        assertEquals(List.of(), itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(3L));
    }

    @Test
    void findAllByRequesterIdNot_return1ItemRequest_added1Request() {
        assertEquals(List.of(itemRequestFromDb),
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(9999L, pageable));
    }

    @Test
    void findAllByRequesterIdNot_returnEmpty_added1Request() {
        assertEquals(List.of(),
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userFromDb.getId(), pageable));
    }
}
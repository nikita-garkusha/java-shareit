package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User userFromDb;
    private Item itemFromDb;
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

        Item item = new Item();
        item.setName("Кирпич");
        item.setDescription("Шлакоблокунь");
        item.setAvailable(true);
        item.setOwner(userFromDb);
        item.setItemRequest(itemRequestFromDb);
        itemFromDb = itemRepository.save(item);

        pageable = PageRequest.of(1 / 20, 20, Sort.by("id").descending());
    }

    @Test
    void findAllByOwnerId_return1Item_added1Items() {
        assertEquals(List.of(itemFromDb), itemRepository.findAllByOwnerId(userFromDb.getId(), pageable));
    }

    @Test
    void findAllByOwnerId_returnEmpty_added1Items() {
        assertEquals(List.of(), itemRepository.findAllByOwnerId(999L, pageable));
    }

    @Test
    void search_return1Item_added1Items() {
        assertEquals(List.of(itemFromDb), itemRepository.search("кир", pageable));
    }

    @Test
    void search_returnEmpty_added1Items() {
        assertEquals(List.of(), itemRepository.search("выфавыф", pageable));
    }

    @Test
    void getItemsByRequestId_return1Item_added1Items() {
        assertEquals(List.of(itemFromDb), itemRepository.findAllByItemRequestId(itemRequestFromDb.getId()));
    }

    @Test
    void getItemsByRequest_returnEmpty_added1Items() {
        assertEquals(List.of(), itemRepository.findAllByItemRequestId(999L));
    }
}
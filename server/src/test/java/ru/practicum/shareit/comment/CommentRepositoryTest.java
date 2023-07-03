package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private Item itemFromDb;
    private Comment commentFromDb;

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        user.setName("vityok");
        user.setEmail("vityok@mail.com");
        User userFromDb = userRepository.save(user);

        Item item = new Item();
        item.setName("Кирпич");
        item.setDescription("Шлакоблокунь");
        item.setAvailable(true);
        item.setOwner(userFromDb);
        itemFromDb = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Нормасик");
        comment.setAuthor(userFromDb);
        comment.setItem(itemFromDb);
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        commentFromDb = commentRepository.save(comment);
    }

    @Test
    void findAllByOwnerId_return1Comment_added1Comment() {
        assertEquals(List.of(commentFromDb), commentRepository.findAllByItemId(itemFromDb.getId()));
    }

    @Test
    void findAllByOwnerId_returnEmpty_added1Comment() {
        assertEquals(List.of(), commentRepository.findAllByItemId(999L));
    }
}
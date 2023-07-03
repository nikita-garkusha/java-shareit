package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentInputDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;
    @Autowired
    private JacksonTester<CommentInputDto> commentInputDtoJacksonTester;

    @Test
    void commentDtoSerializationTest() throws IOException {
        User author = new User();
        author.setId(1L);
        author.setName("Viktor");
        author.setEmail("Ivanich@gmail.com");

        User owner = new User();
        owner.setId(2L);
        owner.setName("Dmitriy");
        owner.setEmail("dm@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Вундервафля");
        item.setDescription("Чудо инженерной мысли");
        item.setAvailable(true);
        item.setOwner(owner);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Ну такое себе, обычная колупалка в пупке");
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comment.setItem(item);
        comment.setAuthor(author);

        CommentDto commentDto = CommentMapper.mapToDto(comment);
        JsonContent<CommentDto> commentDtoJsonContent = commentDtoJacksonTester.write(commentDto);

        assertThat(commentDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(comment.getId()));
        assertThat(commentDtoJsonContent)
                .extractingJsonPathStringValue("$.text").isEqualTo(comment.getText());
        assertThat(commentDtoJsonContent)
                .extractingJsonPathStringValue("$.authorName").isEqualTo(comment.getAuthor().getName());
        assertThat(commentDtoJsonContent)
                .extractingJsonPathStringValue("$.created").isEqualTo(comment.getCreated().toString());
    }

    @Test
    void commentInputDtoDeserializationTest() throws IOException {
        CommentInputDto commentInputDto = new CommentInputDto("Ну такое себе, обычная колупалка в пупке");

        JsonContent<CommentInputDto> commentInputDtoJsonContent = commentInputDtoJacksonTester
                .write(commentInputDto);
        Comment newComment = CommentMapper.mapToComment(commentInputDtoJacksonTester
                .parseObject(commentInputDtoJsonContent.getJson()), new Comment());

        assertThat(newComment).hasFieldOrPropertyWithValue("text",
                commentInputDto.getText());
    }
}


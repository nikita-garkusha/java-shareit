package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.Comment;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment mapToComment(CommentInputDto commentInputDto, Comment comment) {
        comment.setText(commentInputDto.getText());
        comment.setCreated(LocalDateTime.now());

        return comment;
    }
}

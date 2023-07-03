package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.util.List;


@Data
@NoArgsConstructor
public class ItemFullDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private UserShortDto owner;
    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
    private List<CommentDto> comments;

    public ItemFullDto(String name, String description, Boolean available, Long requestId, UserShortDto owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.owner = owner;
    }

    public ItemFullDto(Long id,
                       String name,
                       String description,
                       Boolean available,
                       Long requestId,
                       UserShortDto owner) {
        this(name, description, available, requestId, owner);
        this.id = id;
    }
}

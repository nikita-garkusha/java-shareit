package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    private String description;

    private UserShortDto requester;

    private LocalDateTime created;

    private List<ItemShortDto> items;

    public ItemRequestDto(Long id, String description, UserShortDto requester, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requester = requester;
        this.created = created;
    }
}

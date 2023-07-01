package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "ITEMS", schema = "PUBLIC")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private boolean available;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "REQUEST_ID")
    private ItemRequest itemRequest;
}
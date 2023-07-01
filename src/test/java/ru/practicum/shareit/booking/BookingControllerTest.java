package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingServiceMock;

    private BookingInputDto bookingInputDto1;
    private BookingFullDto bookingFullDto1;
    private BookingFullDto bookingFullDto2;

    @BeforeEach
    void beforeEach() {
        bookingInputDto1 =
                new BookingInputDto(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                        LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS), 1L);

        bookingFullDto1 = new BookingFullDto(1L,
                bookingInputDto1.getStart(),
                bookingInputDto1.getEnd(),
                new ItemShortDto(1L, "dswadsa", "wqewq", true, 2L),
                new UserShortDto(3L, "wqewqew"),
                Status.APPROVED);

        BookingInputDto bookingInputDto2 =
                new BookingInputDto(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                        LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS), 1L);

        bookingFullDto2 = new BookingFullDto(1L,
                bookingInputDto2.getStart(),
                bookingInputDto2.getEnd(),
                new ItemShortDto(1L, "dswadsa", "wqewq", true, 2L),
                new UserShortDto(3L, "wqewqew"),
                Status.APPROVED);
    }

    @SneakyThrows
    @Test
    void create_returnBooking_addedBooking() {
        when(bookingServiceMock.create(1L, bookingInputDto1)).thenReturn(bookingFullDto1);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingInputDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingFullDto1.getId()))
                .andExpect(jsonPath("$.start").value(bookingFullDto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(bookingFullDto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.status").value(bookingFullDto1.getStatus().toString()))

                .andExpect(jsonPath("$.item.id").value(bookingFullDto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingFullDto1.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingFullDto1.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingFullDto1.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingFullDto1.getItem().getRequestId()))

                .andExpect(jsonPath("$.booker.id").value(bookingFullDto1.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingFullDto1.getBooker().getName()));
        verify(bookingServiceMock).create(1L, bookingInputDto1);
    }

    @SneakyThrows
    @Test
    void approve_returnBooking_addedBooking() {
        when(bookingServiceMock.approve(1L, 1L, true)).thenReturn(bookingFullDto1);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingFullDto1.getId()))
                .andExpect(jsonPath("$.start").value(bookingFullDto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(bookingFullDto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.status").value(bookingFullDto1.getStatus().toString()))

                .andExpect(jsonPath("$.item.id").value(bookingFullDto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingFullDto1.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingFullDto1.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingFullDto1.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingFullDto1.getItem().getRequestId()))

                .andExpect(jsonPath("$.booker.id").value(bookingFullDto1.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingFullDto1.getBooker().getName()));
        verify(bookingServiceMock).approve(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    void getById_returnBooking_addedBooking() {
        when(bookingServiceMock.getById(1L, 1L)).thenReturn(bookingFullDto1);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingFullDto1.getId()))
                .andExpect(jsonPath("$.start").value(bookingFullDto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(bookingFullDto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.status").value(bookingFullDto1.getStatus().toString()))

                .andExpect(jsonPath("$.item.id").value(bookingFullDto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingFullDto1.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingFullDto1.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingFullDto1.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingFullDto1.getItem().getRequestId()))

                .andExpect(jsonPath("$.booker.id").value(bookingFullDto1.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingFullDto1.getBooker().getName()));
        verify(bookingServiceMock).getById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getByBookerId_return2Booking_added2Booking() {
        when(bookingServiceMock.getByBookerId(1L, "ALL", 1, 20))
                .thenReturn(List.of(bookingFullDto1, bookingFullDto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingFullDto1.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingFullDto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].end").value(bookingFullDto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].status").value(bookingFullDto1.getStatus().toString()))

                .andExpect(jsonPath("$[0].item.id").value(bookingFullDto1.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookingFullDto1.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookingFullDto1.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookingFullDto1.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookingFullDto1.getItem().getRequestId()))

                .andExpect(jsonPath("$[0].booker.id").value(bookingFullDto1.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookingFullDto1.getBooker().getName()))

                .andExpect(jsonPath("$[1].id").value(bookingFullDto2.getId()))
                .andExpect(jsonPath("$[1].start").value(bookingFullDto2.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].end").value(bookingFullDto2.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].status").value(bookingFullDto2.getStatus().toString()))

                .andExpect(jsonPath("$[1].item.id").value(bookingFullDto2.getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(bookingFullDto2.getItem().getName()))
                .andExpect(jsonPath("$[1].item.description").value(bookingFullDto2.getItem().getDescription()))
                .andExpect(jsonPath("$[1].item.available").value(bookingFullDto2.getItem().getAvailable()))
                .andExpect(jsonPath("$[1].item.requestId").value(bookingFullDto2.getItem().getRequestId()))

                .andExpect(jsonPath("$[1].booker.id").value(bookingFullDto2.getBooker().getId()))
                .andExpect(jsonPath("$[1].booker.name").value(bookingFullDto2.getBooker().getName()));
        verify(bookingServiceMock).getByBookerId(1L, "ALL", 1, 20);
    }


    @SneakyThrows
    @Test
    void getByOwnerId_return2Booking_added2Booking() {
        when(bookingServiceMock.getByOwnerId(1L, "ALL", 1, 20))
                .thenReturn(List.of(bookingFullDto1, bookingFullDto2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingFullDto1.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingFullDto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].end").value(bookingFullDto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].status").value(bookingFullDto1.getStatus().toString()))

                .andExpect(jsonPath("$[0].item.id").value(bookingFullDto1.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookingFullDto1.getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookingFullDto1.getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookingFullDto1.getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookingFullDto1.getItem().getRequestId()))

                .andExpect(jsonPath("$[0].booker.id").value(bookingFullDto1.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookingFullDto1.getBooker().getName()))

                .andExpect(jsonPath("$[1].id").value(bookingFullDto2.getId()))
                .andExpect(jsonPath("$[1].start").value(bookingFullDto2.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].end").value(bookingFullDto2.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].status").value(bookingFullDto2.getStatus().toString()))

                .andExpect(jsonPath("$[1].item.id").value(bookingFullDto2.getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(bookingFullDto2.getItem().getName()))
                .andExpect(jsonPath("$[1].item.description").value(bookingFullDto2.getItem().getDescription()))
                .andExpect(jsonPath("$[1].item.available").value(bookingFullDto2.getItem().getAvailable()))
                .andExpect(jsonPath("$[1].item.requestId").value(bookingFullDto2.getItem().getRequestId()))

                .andExpect(jsonPath("$[1].booker.id").value(bookingFullDto2.getBooker().getId()))
                .andExpect(jsonPath("$[1].booker.name").value(bookingFullDto2.getBooker().getName()));
        verify(bookingServiceMock).getByOwnerId(1L, "ALL", 1, 20);
    }
}

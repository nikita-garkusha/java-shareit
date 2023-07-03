package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingFullDto> bookingFullDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingShortDto> bookingShortDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingInputDto> bookingInputDtoJacksonTester;

    @Test
    void bookingFullDtoSerializationTest() throws IOException {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Viktor");
        booker.setEmail("Ivanich@gmail.com");

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

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        booking.setBooker(booker);

        BookingFullDto bookingFullDto = BookingMapper.mapToFullDto(booking);
        JsonContent<BookingFullDto> bookingFullDtoJsonContent = bookingFullDtoJacksonTester.write(bookingFullDto);

        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(booking.getId()));
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathStringValue("$.start").isEqualTo(booking.getStart().toString());
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathStringValue("$.end").isEqualTo(booking.getEnd().toString());
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathStringValue("$.status").isEqualTo(booking.getStatus().toString());

        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(Math.toIntExact(booking.getItem().getId()));
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathStringValue("$.item.name")
                .isEqualTo(booking.getItem().getName());
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathStringValue("$.item.description")
                .isEqualTo(booking.getItem().getDescription());
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(booking.getItem().isAvailable());

        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(Math.toIntExact(booking.getBooker().getId()));
        assertThat(bookingFullDtoJsonContent)
                .extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(booking.getBooker().getName());
    }

    @Test
    void bookingShortDtoSerializationTest() throws IOException {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Viktor");
        booker.setEmail("Ivanich@gmail.com");

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

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        booking.setBooker(booker);

        BookingShortDto bookingShortDto = BookingMapper.mapToShortDto(booking);
        JsonContent<BookingShortDto> bookingShortDtoJsonContent = bookingShortDtoJacksonTester.write(bookingShortDto);

        assertThat(bookingShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(booking.getId()));
        assertThat(bookingShortDtoJsonContent)
                .extractingJsonPathStringValue("$.start").isEqualTo(booking.getStart().toString());
        assertThat(bookingShortDtoJsonContent)
                .extractingJsonPathStringValue("$.end").isEqualTo(booking.getEnd().toString());
        assertThat(bookingShortDtoJsonContent)
                .extractingJsonPathStringValue("$.status").isEqualTo(booking.getStatus().toString());
        assertThat(bookingShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(Math.toIntExact(booking.getBooker().getId()));
    }

    @Test
    void bookingInputDtoDeserializationTest() throws IOException {
        BookingInputDto bookingInputDto = new BookingInputDto(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS),
                2L);

        JsonContent<BookingInputDto> userInputDtoJsonContent = bookingInputDtoJacksonTester.write(bookingInputDto);
        Booking newBooking = BookingMapper
                .mapToBooking(bookingInputDtoJacksonTester.parseObject(
                                userInputDtoJsonContent.getJson()),
                        new Booking());

        assertThat(newBooking).hasFieldOrPropertyWithValue("start", bookingInputDto.getStart());
        assertThat(newBooking).hasFieldOrPropertyWithValue("end", bookingInputDto.getEnd());
    }
}

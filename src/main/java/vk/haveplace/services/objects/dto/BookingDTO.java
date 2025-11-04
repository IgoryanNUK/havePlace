package vk.haveplace.services.objects.dto;

import lombok.Data;
import vk.haveplace.database.entities.BookingStatus;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDTO {
    private List<Integer> idList;
    private LocalDate date;
    private Time startTime;
    private Time endTime;
    private LocationDTO location;
    private int numberOfPlayers;
    private ClientDTO client;
    private String comments;
    private BookingStatus status;
    private Integer restDayBookingsId;
    private Integer regEventId;
}

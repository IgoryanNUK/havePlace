package vk.haveplace.services.objects.dto;

import lombok.Data;
import vk.haveplace.database.entities.BookingStatus;

import java.sql.Date;
import java.sql.Time;

@Data
public class BookingSimpleDTO {
    private int id;
    private Date date;
    private Time startTime;
    private Time endTime;
    private String locationName;
    private int numberOfPlayers;
    private ClientDTO client;
    private String comments;
    private BookingStatus status;

}

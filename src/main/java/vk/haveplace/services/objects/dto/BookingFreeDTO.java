package vk.haveplace.services.objects.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

@Data
public class BookingFreeDTO {
    private int id;
    private LocalDate date;
    private Time startTime;
    private Time endTime;
    private LocationDTO location;
}

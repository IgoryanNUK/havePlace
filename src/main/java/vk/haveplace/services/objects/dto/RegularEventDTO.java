package vk.haveplace.services.objects.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

@Data
public class RegularEventDTO {
    private int id;
    private String name;
    private LocationSimpleDTO location;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private ClientDTO client;
    private Integer numberOfPlayers;
    private LocalDate startDate;
    private LocalDate endDate;
}

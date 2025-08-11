package vk.haveplace.services.objects.dto;

import lombok.Data;

import java.sql.Time;

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
}

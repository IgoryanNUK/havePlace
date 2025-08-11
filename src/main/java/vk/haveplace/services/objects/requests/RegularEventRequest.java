package vk.haveplace.services.objects.requests;

import lombok.Data;

import java.sql.Time;

@Data
public class RegularEventRequest {
    private int locationId;
    private String name;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private ClientRequest client;
    private Integer numberOfPlayers;
}

package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

@Data
public class RegularEventRequest {
    private int locationId;
    private String name;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private ClientRequest client;
    private Integer numberOfPlayers;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Long adminVkId;
}

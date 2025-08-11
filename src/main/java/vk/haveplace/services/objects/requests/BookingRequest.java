package vk.haveplace.services.objects.requests;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Time;
import java.sql.Date;

@Data
public class BookingRequest {
    @NotNull
    private Integer id;
    private Date date;
    private Time startTime;
    private Time endTime;
    private String locationName;
    @NotNull
    private ClientRequest client;
    private String device;
    @Min(1)
    private int numberOfPlayers;
    private String comments;
}

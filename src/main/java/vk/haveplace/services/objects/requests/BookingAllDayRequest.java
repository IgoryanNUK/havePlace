package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingAllDayRequest {
    @NotNull
    private List<Integer> idList;
    @NotNull
    private ClientRequest client;
    private String device;
    @Min(1)
    private int numberOfPlayers;
    private String comments;
}

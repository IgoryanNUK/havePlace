package vk.haveplace.services.objects.requests;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull
    private Integer id;
    @NotNull
    private ClientRequest client;
    private String device;
    @Min(1)
    private int numberOfPlayers;
    private String comments;
}

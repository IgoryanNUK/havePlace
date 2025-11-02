package vk.haveplace.services.objects.requests;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminBookingRequest {
    @NotNull
    @NotEmpty
    private List<Integer> idList;
    @NotNull
    private ClientRequest client;
    private String device;
    @Min(1)
    private int numberOfPlayers;
    private String comments;
    @NotNull
    private Long adminVkId;

    public String getComments() {
        String pref;
        if (idList.size() > 1) {
            pref = "[НА ВЕСЬ ДЕНЬ] ";
        } else {
            pref = "";
        }

        return pref + comments;
    }
}

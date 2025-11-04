package vk.haveplace.services.objects.requests;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull
    @NotEmpty
    private List<Integer> idList;
    @NotNull
    private ClientRequest client;
    private String device;
    @Min(1)
    private int numberOfPlayers;
    private String comments;

    public String getComments() {
        String pref;
        if (idList.size() > 1) {
            pref = "[НА ВЕСЬ ДЕНЬ] ";
        } else {
            pref = "";
        }

        if (comments == null) {
            return pref;
        } else {
            return pref + comments;
        }
    }
}

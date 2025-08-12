package vk.haveplace.services.objects.dto;

import lombok.Data;
import vk.haveplace.database.entities.BookingStatus;

import java.util.List;

@Data
public class BookingAllDayDTO {
    private List<Integer> idList;
    private LocationDTO location;
    private int numberOfPlayers;
    private ClientDTO client;
    private String comments;
    private BookingStatus status;
}

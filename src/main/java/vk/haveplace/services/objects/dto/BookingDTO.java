package vk.haveplace.services.objects.dto;

import lombok.Data;
import vk.haveplace.database.entities.BookingStatus;

@Data
public class BookingDTO {
    private int id;
    private LocationDTO location;
    private int numberOfPlayers;
    private ClientDTO client;
    private String comments;
    private BookingStatus status;
    private boolean isAvailable;
}

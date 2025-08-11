package vk.haveplace.services.objects.dto;

import lombok.Data;

@Data
public class LocationDTO {
    private int id;
    private String name;
    private int maxNumberOfPlayers;
    private String address;
    private String photos;
}

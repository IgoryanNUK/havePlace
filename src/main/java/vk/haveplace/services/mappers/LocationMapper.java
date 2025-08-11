package vk.haveplace.services.mappers;

import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.services.objects.dto.LocationDTO;
import vk.haveplace.services.objects.dto.LocationSimpleDTO;

public class LocationMapper {
    public static LocationDTO getDTOFromEntity(LocationEntity entity) {
        LocationDTO dto = new LocationDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setMaxNumberOfPlayers(entity.getMax_number_of_players());
        dto.setAddress(entity.getAddress());
        dto.setPhotos(entity.getPhotos());

        return dto;
    }

    public static LocationSimpleDTO getSimpleDTOFromEntity(LocationEntity entity) {
        LocationSimpleDTO dto = new LocationSimpleDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }
}

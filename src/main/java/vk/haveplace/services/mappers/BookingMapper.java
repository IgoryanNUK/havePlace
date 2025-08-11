package vk.haveplace.services.mappers;

import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingFreeDTO;
import vk.haveplace.services.objects.dto.BookingSimpleDTO;

public class BookingMapper {

    public static BookingSimpleDTO getSimpleDTOFromEntity(BookingEntity entity) {
        BookingSimpleDTO dto = new BookingSimpleDTO();

        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setLocationName(entity.getLocation().getName());
        dto.setNumberOfPlayers(entity.getNumberOfPlayers() == null ? 0 : entity.getNumberOfPlayers());
        dto.setClient(entity.getClient() == null ? null : ClientMapper.getDTOFromEntity(entity.getClient()));
        dto.setComments(entity.getComments());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    public static BookingFreeDTO getFreeDTOFromEntity(BookingEntity entity) {
        BookingFreeDTO dto = new BookingFreeDTO();

        dto.setId(entity.getId());
        dto.setDate(entity.getDate().toLocalDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setLocation(LocationMapper.getDTOFromEntity(entity.getLocation()));

        return dto;
    }

    public static BookingDTO getDTOFromEntity(BookingEntity entity) {
        BookingDTO dto = new BookingDTO();

        dto.setId(entity.getId());
        dto.setLocation(LocationMapper.getDTOFromEntity(entity.getLocation()));
        dto.setNumberOfPlayers(entity.getNumberOfPlayers() == null ? 0 : entity.getNumberOfPlayers());
        dto.setClient(entity.getClient() == null ? null : ClientMapper.getDTOFromEntity(entity.getClient()));
        dto.setComments(entity.getComments());
        dto.setStatus(entity.getStatus());
        dto.setAvailable(entity.getIsAvailable());

        return dto;
    }
}

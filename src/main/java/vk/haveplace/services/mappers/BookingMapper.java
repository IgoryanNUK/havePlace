package vk.haveplace.services.mappers;

import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingFreeDTO;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingFreeDTO getFreeDTOFromEntity(BookingEntity entity) {
        BookingFreeDTO dto = new BookingFreeDTO();

        dto.setId(entity.getId());
        dto.setLocation(LocationMapper.getDTOFromEntity(entity.getLocation()));

        return dto;
    }

    public static BookingDTO getDTOFromEntity(List<BookingEntity> entityList) {
        BookingDTO dto = new BookingDTO();
        BookingEntity entity = entityList.getFirst();

        dto.setIdList(new ArrayList<>(2));
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setLocation(LocationMapper.getDTOFromEntity(entity.getLocation()));
        dto.setNumberOfPlayers(entity.getNumberOfPlayers() == null ? 0 : entity.getNumberOfPlayers());
        dto.setClient(entity.getClient() == null ? null : ClientMapper.getDTOFromEntity(entity.getClient()));
        dto.setComments(entity.getComments());
        dto.setStatus(entity.getStatus());

        for (BookingEntity en : entityList) {
            dto.getIdList().add(en.getId());
        }

        if (entityList.size() > 1) {
            BookingEntity last = entityList.getLast();

            dto.setStartTime(TimeSlot.min(dto.getStartTime(),
                    last.getStartTime()));
            dto.setEndTime(TimeSlot.max(dto.getEndTime(),
                    last.getEndTime()));
        }


        return dto;
    }
}

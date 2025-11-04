package vk.haveplace.services.mappers;

import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.database.entities.RegularEventEntity;
import vk.haveplace.services.objects.dto.RegularEventDTO;
import vk.haveplace.services.objects.requests.RegularEventRequest;
import vk.haveplace.services.objects.requests.RegularEventUpdateRequest;

import java.sql.Date;

public class RegularEventMapper {

    public static RegularEventEntity getEntityFromRequest(RegularEventRequest request,
                                                          ClientEntity clientEntity, LocationEntity locationEntity) {
        RegularEventEntity entity = new RegularEventEntity();
        
        if (request instanceof RegularEventUpdateRequest) {
            entity.setId(((RegularEventUpdateRequest) request).getId());
        }

        entity.setStartTime(request.getStartTime());
        entity.setName(request.getName());
        entity.setEndTime(request.getEndTime());
        entity.setNumberOfPlayers(request.getNumberOfPlayers());
        entity.setDayOfWeek(request.getDayOfWeek());
        entity.setClient(clientEntity);
        entity.setLocation(locationEntity);
        entity.setStartDate(Date.valueOf(request.getStartDate()));
        entity.setEndDate(Date.valueOf(request.getEndDate()));

        return entity;
    }

    public static RegularEventDTO getDTOFromEntity(RegularEventEntity entity) {
        RegularEventDTO dto = new RegularEventDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(LocationMapper.getSimpleDTOFromEntity(entity.getLocation()));
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setClient(ClientMapper.getDTOFromEntity(entity.getClient()));
        dto.setNumberOfPlayers(entity.getNumberOfPlayers());
        dto.setStartDate(entity.getStartDate().toLocalDate());
        dto.setEndDate(entity.getEndDate().toLocalDate());

        return dto;
    }
}

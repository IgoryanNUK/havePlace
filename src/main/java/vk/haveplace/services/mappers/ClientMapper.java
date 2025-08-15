package vk.haveplace.services.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.services.ClientService;
import vk.haveplace.services.objects.Client;
import vk.haveplace.services.objects.dto.ClientDTO;
import vk.haveplace.services.objects.requests.ClientRequest;


public class ClientMapper {

    public static ClientEntity getEntityFromDTO(ClientDTO dto) {
        ClientEntity ent = new ClientEntity();

        ent.setId(dto.getId());
        ent.setName(dto.getName());
        ent.setPhone(dto.getPhone());
        ent.setVkId(dto.getVkId());

        return ent;
    }

    public static ClientEntity getEntityFromRequest(ClientRequest request) {
        ClientEntity entity = new ClientEntity();

        entity.setName(request.getName());
        entity.setPhone(request.getPhone());
        entity.setVkId(request.getVkId());
        return entity;
    }

    public static ClientDTO getDTOFromEntity(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        dto.setVkId(entity.getVkId());

        return dto;
    }
}

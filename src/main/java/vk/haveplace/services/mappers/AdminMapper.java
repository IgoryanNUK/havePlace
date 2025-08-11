package vk.haveplace.services.mappers;

import vk.haveplace.database.entities.AdminEntity;
import vk.haveplace.services.objects.dto.AdminDTO;
import vk.haveplace.services.objects.requests.AdminRequest;
import vk.haveplace.services.objects.requests.AdminUpdateRequest;

public class AdminMapper {
    public static AdminEntity getEntityFromRequest(AdminRequest req) {
        AdminEntity entity = new AdminEntity();

        entity.setName(req.getName());
        entity.setVkId(req.getVkId());
        entity.setRole(req.getRole());

        return entity;
    }

    public static AdminDTO getDTOFromEntity(AdminEntity entity) {
        AdminDTO dto;
        if (entity == null) {
            dto = null;
        } else {
            dto = new AdminDTO();

            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setVkId(entity.getVkId());
            dto.setRole(entity.getRole());
        }

        return dto;
    }

    public static AdminEntity getEntityFromUpdateRequest(AdminUpdateRequest req) {
        AdminEntity entity = getEntityFromRequest(req);
        entity.setId(req.getId());

        return entity;
    }
}

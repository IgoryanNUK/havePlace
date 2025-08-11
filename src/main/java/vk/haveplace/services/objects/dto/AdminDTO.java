package vk.haveplace.services.objects.dto;

import lombok.Data;
import vk.haveplace.database.entities.Role;

@Data
public class AdminDTO {
    private Integer id;
    private String name;
    private String vkLink;
    private Role role;
}

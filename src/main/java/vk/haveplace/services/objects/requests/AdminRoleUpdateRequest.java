package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vk.haveplace.database.entities.Role;

@Data
public class AdminRoleUpdateRequest {
    @NotNull
    private Integer id;
    @NotNull
    private Role role;
}

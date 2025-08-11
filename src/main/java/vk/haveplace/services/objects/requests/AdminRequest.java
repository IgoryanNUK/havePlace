package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vk.haveplace.database.entities.Role;

@Data
public class AdminRequest {
    @NotNull
    private String name;

    @NotNull
    private String vkLink;

    @NotNull
    private Role role;
}

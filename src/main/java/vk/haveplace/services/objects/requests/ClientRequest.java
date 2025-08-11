package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientRequest {
    @NotNull
    private String name;
    private String phone;
    private String vkLink;
}

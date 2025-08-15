package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vk.haveplace.services.objects.Client;

@Data
public class ClientRequest implements Client {
    @NotNull
    private String name;
    private String phone;
    @NotNull
    private Long vkId;
}

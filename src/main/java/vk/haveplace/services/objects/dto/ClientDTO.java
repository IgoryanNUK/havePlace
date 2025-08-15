package vk.haveplace.services.objects.dto;

import lombok.Data;
import vk.haveplace.services.objects.Client;

@Data
public class ClientDTO implements Client {
    private int id;
    private String name;
    private String phone;
    private Long vkId;
}

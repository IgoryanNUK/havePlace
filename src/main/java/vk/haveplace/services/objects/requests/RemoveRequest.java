package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RemoveRequest {
    @NotNull
    @NotEmpty
    private List<Integer> idList;
    @NotNull
    private ClientRequest client;
}

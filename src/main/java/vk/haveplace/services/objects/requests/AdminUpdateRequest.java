package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateRequest extends AdminRequest {
    @NotNull
    private Integer id;
}

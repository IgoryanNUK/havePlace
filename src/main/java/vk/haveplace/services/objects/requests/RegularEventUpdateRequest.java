package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RegularEventUpdateRequest extends RegularEventRequest {
    @NotNull
    private Integer id;
}

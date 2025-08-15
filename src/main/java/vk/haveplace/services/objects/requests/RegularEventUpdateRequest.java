package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class RegularEventUpdateRequest extends RegularEventRequest {
    @NotNull
    private Integer id;
}

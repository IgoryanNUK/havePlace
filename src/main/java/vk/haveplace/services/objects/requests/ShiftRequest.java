package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.aspectj.weaver.ast.Not;

@Data
public class ShiftRequest {
    @NotNull
    private long adminId;
    @NotNull
    private DateAndTimesRequest shift;
}

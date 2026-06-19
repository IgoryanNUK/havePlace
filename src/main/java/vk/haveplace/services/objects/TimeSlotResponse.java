package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeSlotResponse {
    private Integer createdCount;
    private ConflictResponse<TimeSlot> conflicts;
}

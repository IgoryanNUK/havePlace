package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotWithPrice extends TimeSlot {
    private Integer price;

    public TimeSlotWithPrice(TimeSlot timeSlot, Integer price) {
        this.setStart(timeSlot.getStart());
        this.setEnd(timeSlot.getEnd());
        this.price = price;
    }
}

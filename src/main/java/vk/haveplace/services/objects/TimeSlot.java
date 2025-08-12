package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {
    private Time start;
    private Time end;

    public static TimeSlot unite(TimeSlot slot1, TimeSlot slot2) {
        TimeSlot result = new TimeSlot(min(slot1.getStart(), slot2.getStart()),
                max(slot1.getEnd(), slot2.getEnd()));

        return result;
    }

    public static Time max(Time time1, Time time2) {
        if (time1.compareTo(time2) > 0) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Time min(Time time1, Time time2) {
        if (time1.compareTo(time2) > 0) {
            return time2;
        } else {
            return time1;
        }
    }
}

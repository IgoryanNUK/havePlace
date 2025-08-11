package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateAndTimesDTO {
    private Date date;
    private Time startTime;
    private Time endTime;
}

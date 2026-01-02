package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vk.haveplace.database.entities.LocationEntity;

import java.sql.Date;
import java.sql.Time;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class LocationDateAndTimesDTO extends TimeSlot {
    private Date date;
    private Time startTime;
    private Time endTime;
    private LocationEntity location;
}

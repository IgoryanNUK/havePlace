package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vk.haveplace.database.entities.LocationEntity;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDateAndTimesDTO {
    private Date date;
    private Time startTime;
    private Time endTime;
    private LocationEntity location;
}

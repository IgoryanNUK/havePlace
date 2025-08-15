package vk.haveplace.services.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import vk.haveplace.database.entities.LocationEntity;

import java.sql.Date;

@Data
@AllArgsConstructor
public class DateAndLocation {
    private Date date;
    private LocationEntity location;
}

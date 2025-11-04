package vk.haveplace.services.objects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateAndTimesDTO {
    private LocalDate date;
    private Time startTime;
    private Time endTime;
}

package vk.haveplace.services.objects.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateAndTimesRequest {
    @NotNull
    private LocalDate date;
    @NotNull
    private Time startTime;
    @NotNull
    private Time endTime;
}


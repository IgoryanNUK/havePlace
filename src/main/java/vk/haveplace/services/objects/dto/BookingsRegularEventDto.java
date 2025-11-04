package vk.haveplace.services.objects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import vk.haveplace.services.objects.DateAndTimesDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingsRegularEventDto {
    private final List<Integer> ok;
    private List<LocalDateAndTimesDTO> conflicts;

    public static BookingsRegularEventDto zero() {
        return new BookingsRegularEventDto(Collections.emptyList());
    }
}

package vk.haveplace.services.objects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookingFreeAllDayDTO {
    private List<Integer> idList;
    private LocationDTO location;
}

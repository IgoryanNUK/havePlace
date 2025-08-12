package vk.haveplace.services.objects.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookingFreeAllDayDTO {
    private List<Integer> idList;
    private LocationDTO location;
}

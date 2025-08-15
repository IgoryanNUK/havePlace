package vk.haveplace.services.objects.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingFreeDTO {
    private List<Integer> idList;
    private LocationDTO location;
}

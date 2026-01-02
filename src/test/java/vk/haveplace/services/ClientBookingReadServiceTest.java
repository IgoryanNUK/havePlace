package vk.haveplace.services;


import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.springframework.beans.factory.annotation.Autowired;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.services.objects.LocationDateAndTimesDTO;
import vk.haveplace.services.objects.TimeSlot;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ClientBookingReadServiceTest {

//    @Autowired
//    private ClientBookingReadService service;
//
//    @Mock
//    private PriceService priceService;
//    @Mock
//    private BookingRepository bookingRepository;
//
//
//    @Test
//    public void giv_when_freeTimeSlots_validFreeTimeSlots() {
//
//        when(bookingRepository.findFreeTimeSlotsWithLocationUntil(any(Date.class)))
//                .thenAnswer(List.of(
//                        new LocationDateAndTimesDTO(Date.valueOf("2026-01-01", "18:00:00", ""))
//                ))
//        ;
//
//        when(priceService.getPrice(any()))
//
//        Map<LocalDate, List<TimeSlot>> response = service.getFreeTimeSlots();
//
//
//    }


}

package vk.haveplace.controllers;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vk.haveplace.services.ClientBookingReadService;
import vk.haveplace.services.ClientBookingWriteService;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingFreeAllDayDTO;
import vk.haveplace.services.objects.dto.BookingFreeDTO;
import vk.haveplace.services.objects.requests.BookingRequest;
import vk.haveplace.services.objects.requests.ClientRequest;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;
import vk.haveplace.services.objects.requests.RemoveRequest;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientBookingWriteService bookingWriteService;
    private final ClientBookingReadService bookingReadService;

    public ClientController(ClientBookingWriteService bookingWriteService,
                            ClientBookingReadService bookingReadService) {
        this.bookingWriteService = bookingWriteService;
        this.bookingReadService = bookingReadService;
    }

    @PostMapping("/book")
    public BookingDTO book(@RequestBody @Validated BookingRequest booking) {
        return bookingWriteService.book(booking);
    }

    @GetMapping("/timeSlots")
    public Map<LocalDate, List<TimeSlot>> getFreeTimeSlots() {
        return bookingReadService.getFreeTimeSlots();
    }

    @GetMapping("/bookings/{date}/{startTime}/{endTime}")
    public Map<String, BookingFreeDTO> getFreeBookings(@PathVariable LocalDate date, @PathVariable Time startTime,
                                                       @PathVariable Time endTime) {
        return bookingReadService.getFreeBookings(new DateAndTimesRequest(date, startTime, endTime));
    }

    @GetMapping("/my/{vkId}")
    public List<BookingDTO> getMyBookings(@PathVariable Long vkId) {
        return bookingReadService.getBookingsByClient(vkId);
    }
}

package vk.haveplace.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vk.haveplace.services.AdminBookingReadService;
import vk.haveplace.services.AdminBookingWriteService;
import vk.haveplace.services.RegularEventService;
import vk.haveplace.services.TimeSlotsService;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.RegularEventDTO;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;
import vk.haveplace.services.objects.requests.RegularEventRequest;
import vk.haveplace.services.objects.requests.RegularEventUpdateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TimeSlotsService timeSlotsService;
    private final AdminBookingReadService bookingReadService;
    private final AdminBookingWriteService bookingWriteService;

    @Autowired
    public AdminController(TimeSlotsService timeSlotsService, AdminBookingReadService bookingReadService,
                           AdminBookingWriteService bookingWriteService) {
        this.timeSlotsService = timeSlotsService;
        this.bookingReadService = bookingReadService;
        this.bookingWriteService = bookingWriteService;
    }

    @PostMapping("/timeSlots/{endDate}")
    public ResponseEntity<Integer> setTimeSlots(@RequestBody @NotNull Map<String, List<TimeSlot>> timeMap, @PathVariable LocalDate endDate) {
        Integer ans = timeSlotsService.setTimeSlotsForPeriod(endDate, timeMap);
        return new ResponseEntity<>(ans, HttpStatus.CREATED);
    }

    @GetMapping("/bookings")
    public Map<String, BookingDTO> getAllBookings(@RequestBody @Validated DateAndTimesRequest dateAndTimes) {
        return bookingReadService.getAllBookings(dateAndTimes);
    }

    @GetMapping("/timeSlots")
    public Map<LocalDate, List<TimeSlot>> getTimeSlots() {
        return bookingReadService.getTimeSlots();
    }

    @PostMapping("/confirm/{bookingId}")
    public Boolean confirm(@PathVariable int bookingId) {
        return bookingWriteService.confirmBooking(bookingId);
    }

    @PostMapping("/lock/{bookingId}")
    public Boolean lock(@PathVariable int bookingId) {
        return bookingWriteService.lock(bookingId);
    }
}

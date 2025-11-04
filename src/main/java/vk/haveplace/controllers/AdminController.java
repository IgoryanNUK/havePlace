package vk.haveplace.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vk.haveplace.services.admin.AdminBookingReadService;
import vk.haveplace.services.admin.AdminBookingWriteService;
import vk.haveplace.services.admin.TimeSlotsService;
import vk.haveplace.services.objects.FailsCount;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingsRegularEventDto;
import vk.haveplace.services.objects.requests.*;

import java.sql.Time;
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
    public ResponseEntity<Integer> setTimeSlots(@RequestBody @NotNull Map<String, List<TimeSlot>> timeMap,
                                                @PathVariable LocalDate endDate) {
        Integer ans = timeSlotsService.setTimeSlotsForPeriod(endDate, timeMap);
        return new ResponseEntity<>(ans, HttpStatus.CREATED);
    }

    @GetMapping("/bookings/{date}/{startTime}/{endTime}")
    public Map<String, BookingDTO> getAllBookings(@PathVariable LocalDate date, @PathVariable Time startTime,
                                                  @PathVariable Time endTime) {
        return bookingReadService.getAllBookings(new DateAndTimesRequest(date, startTime, endTime));
    }

    @GetMapping("/new")
    public List<BookingDTO> getNew() {
        return bookingReadService.getNew();
    }

    @GetMapping("/timeSlots")
    public Map<LocalDate, List<TimeSlot>> getTimeSlots() {
        return bookingReadService.getTimeSlots();
    }

    @PostMapping("/confirm/{bookingId}/{adminVkId}")
    public Boolean confirm(@PathVariable int bookingId, @PathVariable long adminVkId) {
        return bookingWriteService.confirmBooking(bookingId, adminVkId);
    }

    @PostMapping("/lock/{bookingId}/{adminVkId}")
    public Boolean lock(@PathVariable int bookingId, @PathVariable long adminVkId) {
        return bookingWriteService.lock(bookingId, adminVkId);
    }

//    @PostMapping("/bookEvent/{id}")
//    public FailsCount bookRegularEvent(@PathVariable int id) {
//        return new FailsCount(bookingWriteService.bookRegularEvent(id));
//    }

    @PostMapping("/event/check")
    public BookingsRegularEventDto check(@RequestBody @Validated RegularEventRequest request) {
        return bookingReadService.checkRegularEventBookings(request);
    }


    @GetMapping("/bookings/{startDate}/{endDate}")
    public Map<LocalDate, Map<String, Map<String, BookingDTO>>> getBookingsForPeriod(
            @PathVariable LocalDate startDate, @PathVariable LocalDate endDate
    ) {
        return bookingReadService.getBookingsForPeriod(startDate, endDate);
    }

    @PostMapping("/update")
    public BookingDTO update(@RequestBody @Validated AdminBookingRequest booking) {
        return bookingWriteService.update(booking);
    }

    @PostMapping("/book")
    public BookingDTO book(@RequestBody @Validated AdminBookingRequest booking) {
        return bookingWriteService.book(booking);
    }

    @DeleteMapping("")
    public boolean remove(@RequestBody @Validated RemoveRequest removeRequest) {
        return bookingWriteService.remove(removeRequest.getIdList(), removeRequest.getClient(), removeRequest.getAdminVkId());
    }
}

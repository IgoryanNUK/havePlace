package vk.haveplace.controllers;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vk.haveplace.services.ClientBookingReadService;
import vk.haveplace.services.ClientBookingWriteService;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.BookingFreeDTO;
import vk.haveplace.services.objects.dto.BookingSimpleDTO;
import vk.haveplace.services.objects.requests.BookingRequest;
import vk.haveplace.services.objects.requests.ClientRequest;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;

import java.sql.Time;
import java.time.LocalDate;
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
    public BookingSimpleDTO book(@RequestBody @Validated BookingRequest booking) {
        return bookingWriteService.setNewBooking(booking);
    }

    @PostMapping("/update")
    public BookingSimpleDTO update(@RequestBody @Validated BookingRequest booking) {
        return bookingWriteService.update(booking);
    }

    @DeleteMapping("/{id}")
    public boolean remove(@PathVariable int id, @RequestBody ClientRequest client) {
        return bookingWriteService.remove(id, client);
    }

    @GetMapping("/timeSlots")
    public Map<LocalDate, List<TimeSlot>> getFreeTimeSlots() {
        return bookingReadService.getFreeTimeSlots();
    }

    @GetMapping("/bookings")
    public Map<String, BookingFreeDTO> getFreeBookings(@RequestBody @Validated DateAndTimesRequest dateAndTime) {
        return bookingReadService.getFreeBookings(dateAndTime);
    }

    @GetMapping("/my")
    public List<BookingSimpleDTO> getMyBookings(@RequestBody @Validated ClientRequest client) {
        return bookingReadService.getBookingsByClient(client.getVkLink());
    }
}

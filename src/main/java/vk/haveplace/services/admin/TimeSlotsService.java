package vk.haveplace.services.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.LocationRepository;
import vk.haveplace.database.RegularEventRepository;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.BookingStatus;
import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.database.entities.RegularEventEntity;
import vk.haveplace.services.ClientBookingWriteService;
import vk.haveplace.services.objects.TimeSlot;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class TimeSlotsService {
    private final LocationRepository locationRepository;
    private final BookingRepository bookingRepository;
    private final RegularEventRepository regularEventRepository;
    private final ClientBookingWriteService bookingService;

    public TimeSlotsService(BookingRepository bookingRepository, LocationRepository locationRepository,
                            RegularEventRepository regularEventRepository, ClientBookingWriteService bookingService) {
        this.bookingRepository = bookingRepository;
        this.locationRepository = locationRepository;
        this.regularEventRepository = regularEventRepository;
        this.bookingService = bookingService;
    }

    /**
     * Создает слоты для бронирования, резервируя места для регулярных событий.
     * @param endDate
     * @param timeMap
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int setTimeSlotsForPeriod(LocalDate endDate, Map<String, List<TimeSlot>> timeMap) {
        LocalDate startDate;
        try {
            startDate = bookingRepository.findFirstByOrderByDateDesc().orElseThrow().getDate().toLocalDate();
        } catch (NoSuchElementException e) {
            startDate = LocalDate.now();
        }

        List<RegularEventEntity> regEvents = regularEventRepository.findAll();

        int totalCount = startDate.plusDays(1).datesUntil(endDate.plusDays(1))
                .mapToInt(d -> setTimeSlotsForDay(d, timeMap,
                        regEvents.stream().filter(e -> e.getDayOfWeek().equals(d.getDayOfWeek().toString())).toList()))
                .sum();

        return totalCount;
    }

    private int setTimeSlotsForDay(LocalDate d, Map<String, List<TimeSlot>> timeMap,
                                   List<RegularEventEntity> regEvents) {
        int totalCount = 0;

        for(TimeSlot timeSlot : timeMap.get(d.getDayOfWeek().toString())) {
            for(LocationEntity location : locationRepository.findAll()) {
                RegularEventEntity entity = regEvents.stream()
                        .filter(e -> e.getLocation().equals(location)
                                && e.getStartTime().equals(timeSlot.getStart())
                                && e.getEndTime().equals(timeSlot.getEnd()))
                        .findFirst().orElse(null);
                totalCount += setNewTimeSlot(Date.valueOf(d), timeSlot, location, entity);
            }
        }

        return totalCount;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private int setNewTimeSlot(Date date, TimeSlot timeSlot, LocationEntity location, RegularEventEntity regEvent) {
        BookingEntity slot = new BookingEntity();

        slot.setDate(date);
        slot.setStartTime(timeSlot.getStart());
        slot.setEndTime(timeSlot.getEnd());
        slot.setLocation(location);
        slot.setStatus(BookingStatus.FREE);
        slot.setIsAvailable(true);

        if (regEvent != null) {
            slot.setClient(regEvent.getClient());
            slot.setNumberOfPlayers(regEvent.getNumberOfPlayers());
            slot.setComments(regEvent.getName());
            slot.setStatus(BookingStatus.CONFIRMED);
            slot.setIsAvailable(false);
        }

        bookingRepository.save(slot);

        return 1;
    }

}

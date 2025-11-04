package vk.haveplace.services.admin;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.AdminEntity;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.exceptions.BookingNotFound;
import vk.haveplace.services.mappers.AdminMapper;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.objects.DateAndTimesDTO;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.AdminDTO;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingsRegularEventDto;
import vk.haveplace.services.objects.dto.LocalDateAndTimesDTO;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;
import vk.haveplace.services.objects.requests.RegularEventRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminBookingReadService {
    private final BookingRepository bookingRepository;
    private final int BOOKING_CHECK_WEEKS_PERIOD = 50;

    public AdminBookingReadService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Map<String, BookingDTO> getAllBookings(DateAndTimesRequest dateAndTimes) {
        List<BookingEntity> entityList = bookingRepository.findAllByDateAndStartTimeAndEndTime(Date.valueOf(dateAndTimes.getDate()),
                dateAndTimes.getStartTime(), dateAndTimes.getEndTime());

        Map<String, BookingDTO> result = new HashMap<>(entityList.size());
        for (BookingEntity entity : entityList) {
            result.put(entity.getLocation().getName(), BookingMapper.getDTOFromEntity(entity, b -> getRestDayBookingId(entity)));
        }

        return result;
    }

    @Transactional
    public List<BookingDTO> getNew() {
        List<BookingEntity> entityList = bookingRepository.findNew();

        List<BookingDTO> list = new ArrayList<>(entityList.size());
        for (BookingEntity entity : entityList) {
            list.add(BookingMapper.getDTOFromEntity(entity));
        }

        return list;
    }

    @Transactional
    public Map<LocalDate, List<TimeSlot>> getTimeSlots() {
        LocalDate endDate = LocalDate.now().plusWeeks(BOOKING_CHECK_WEEKS_PERIOD);

        List<DateAndTimesDTO> list = bookingRepository.findTimeSlotsUntil(Date.valueOf(endDate));

        Map<LocalDate, List<TimeSlot>> result = new TreeMap<>();
        for (DateAndTimesDTO dateAndTimes : list) {
            LocalDate date = dateAndTimes.getDate().toLocalDate();

            if (result.containsKey(date)) {
                result.get(date).add(new TimeSlot(dateAndTimes.getStartTime(), dateAndTimes.getEndTime()));
            } else {
                List<TimeSlot> l = new ArrayList<>(2);
                l.add(new TimeSlot(dateAndTimes.getStartTime(), dateAndTimes.getEndTime()));
                result.put(date, l);
            }
        }

        return result;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AdminDTO getAdminByShift(DateAndTimesRequest shift) {
        List<BookingEntity> bookingList = bookingRepository.findAllByDateAndStartTimeAndEndTime(Date.valueOf(shift.getDate()),
                shift.getStartTime(), shift.getEndTime());

        if (bookingList.isEmpty()) {
            throw new BookingNotFound(shift.toString());
        }

        AdminEntity entity = bookingList.getFirst().getAdmin();

        return AdminMapper.getDTOFromEntity(entity);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    private Integer getRestDayBookingId(BookingEntity entity) {
        return bookingRepository.findAllByDateAndLocation(entity.getDate(), entity.getLocation())
                .stream().filter(b -> !Objects.equals(b.getId(), entity.getId()) && b.getIsAvailable())
                .map(BookingEntity::getId).findFirst().orElse(null);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<LocalDate, Map<String, Map<String, BookingDTO>>> getBookingsForPeriod(LocalDate startTime, LocalDate endTime) {
        List<BookingEntity> bookingEntityList = bookingRepository
                .findAllFromStartDateToEndDateOrderByDate(Date.valueOf(startTime), Date.valueOf(endTime));

        return bookingEntityList.stream()
                .map(b -> BookingMapper.getDTOFromEntity(b, e -> getRestDayBookingId(b)))
                .collect(Collectors.groupingBy(
                        BookingDTO::getDate,
                        TreeMap::new,
                        Collectors.groupingBy(
                                b -> b.getStartTime() + "-" + b.getEndTime(),
                                TreeMap::new,
                                Collectors.toMap(
                                        b -> b.getLocation().getName(),
                                        Function.identity()
                                )
                        )
                ));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingsRegularEventDto checkRegularEventBookings(RegularEventRequest request) {
        BookingsRegularEventDto response;

        LocalDate startDate = request.getStartDate().isAfter(LocalDate.now().minusDays(1))
                && request.getStartDate().equals(LocalDate.now().minusDays(1))
                ? request.getStartDate()
                : LocalDate.now();


        if (request.getStartDate().isBefore(request.getEndDate())) {


            List<LocalDate> dates = startDate.datesUntil(request.getEndDate().plusDays(1))
                    .filter(d -> d.getDayOfWeek().toString().equals(request.getDayOfWeek()))
                    .toList();

            List<Integer> ok = new LinkedList<>();
            List<LocalDateAndTimesDTO> conflicts = new ArrayList<>();
            for (LocalDate date : dates) {
                List<BookingEntity> allBookingsBySetting = bookingRepository.findAllByDate(Date.valueOf(date)).stream()
                        .filter(e -> e.getLocation().getId() == request.getLocationId() //find bookings by location and time
                        && (e.getStartTime().equals(request.getStartTime()) && e.getEndTime().compareTo(request.getEndTime()) <=0
                        || e.getStartTime().compareTo(request.getStartTime()) >= 0 && e.getEndTime().equals(request.getEndTime())))
                        .toList();

                for (BookingEntity entity : allBookingsBySetting) {
                    if (entity.getIsAvailable()) {
                        ok.add(entity.getId());
                    } else {
                        conflicts.add(new LocalDateAndTimesDTO(entity.getDate().toLocalDate(), entity.getStartTime(), entity.getEndTime()));
                    }
                }
            }

            response = new BookingsRegularEventDto(ok, conflicts);
        } else {
            response = BookingsRegularEventDto.zero();
        }

        return response;
    }
}

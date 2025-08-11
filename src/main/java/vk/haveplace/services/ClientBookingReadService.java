package vk.haveplace.services;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.objects.DateAndTimesDTO;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.dto.BookingFreeDTO;
import vk.haveplace.services.objects.dto.BookingSimpleDTO;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class ClientBookingReadService {

    private final int ALLOWED_BOOKING_PERIOD = 5;
    private final BookingRepository bookingRepository;
    private final ClientService clientService;

    public ClientBookingReadService(BookingRepository bookingRepository, ClientService clientService) {
        this.bookingRepository = bookingRepository;
        this.clientService = clientService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<LocalDate, List<TimeSlot>> getFreeTimeSlots() {
        LocalDate endDate = LocalDate.now().plusWeeks(ALLOWED_BOOKING_PERIOD);

        List<DateAndTimesDTO> entityList = bookingRepository.findFreeTimeSlotsUntil(Date.valueOf(endDate));

        Map<LocalDate, List<TimeSlot>> map = new TreeMap<>();
        for (DateAndTimesDTO dto : entityList) {

            TimeSlot timeSlot = new TimeSlot(dto.getStartTime(), dto.getEndTime());
            LocalDate date = dto.getDate().toLocalDate();
            if (map.containsKey(date)) {
                map.get(date).add(timeSlot);
            } else {
                List<TimeSlot> list = new ArrayList<>(2);
                list.add(timeSlot);
                map.put(date, list);
            }
        }

        return map;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<String, BookingFreeDTO> getFreeBookings(DateAndTimesRequest dateAndTimes) {

        List<BookingEntity> entityList = bookingRepository.findFreeBookingsUntil(Date.valueOf(dateAndTimes.getDate()),
                dateAndTimes.getStartTime(), dateAndTimes.getEndTime());

        Map<String, BookingFreeDTO> result = new HashMap<>(3);
        for (BookingEntity entity : entityList) {
            result.put(entity.getLocation().getName(), BookingMapper.getFreeDTOFromEntity(entity));
        }

        return result;
    }

    @Transactional
    public List<BookingSimpleDTO> getBookingsByClient(String vkLink) {
        ClientEntity clientEntity = clientService.getEntityByVkLink(vkLink);

        if (clientEntity == null) {
            return null;
        }

        List<BookingEntity> entityList = bookingRepository.findAllByClientOrderById(clientEntity);

        List<BookingSimpleDTO> result = new ArrayList<>(entityList.size());
        for (BookingEntity entity : entityList) {
            result.add(BookingMapper.getSimpleDTOFromEntity(entity));
        }

        return result;
    }
}

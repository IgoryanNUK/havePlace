package vk.haveplace.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.objects.DateAndTimesDTO;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.TimeSlotWithPrice;
import vk.haveplace.services.objects.dto.BookingFreeAllDayDTO;
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
    private final PriceService priceService;

    public ClientBookingReadService(BookingRepository bookingRepository, ClientService clientService,
                                    PriceService priceService) {
        this.bookingRepository = bookingRepository;
        this.clientService = clientService;
        this.priceService = priceService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<LocalDate, List<TimeSlot>> getFreeTimeSlots() {
        LocalDate endDate = LocalDate.now().plusWeeks(ALLOWED_BOOKING_PERIOD);

        List<DateAndTimesDTO> entityList = bookingRepository.findFreeTimeSlotsUntil(Date.valueOf(endDate));

        Map<LocalDate, List<TimeSlot>> map = new TreeMap<>();
        Map<TimeSlot, Integer> prices = priceService.getPriceMap();
        for (DateAndTimesDTO dto : entityList) {
            TimeSlot key = new TimeSlot(dto.getStartTime(), dto.getEndTime());
            TimeSlot timeSlot = new TimeSlotWithPrice(key, prices.get(key));

            LocalDate date = dto.getDate().toLocalDate();
            if (map.containsKey(date)) {
                List<TimeSlot> list = map.get(date);

                list.add(timeSlot);

                //добавления слота на весь день
                timeSlot = TimeSlot.unite(list.getFirst(), list.getLast());
                timeSlot = new TimeSlotWithPrice(timeSlot, prices.get(timeSlot));
                list.add(timeSlot);
            } else {
                List<TimeSlot> list = new ArrayList<>(3);
                list.add(timeSlot);
                map.put(date, list);
            }
        }

        return map;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<String, BookingFreeDTO> getFreeBookings(DateAndTimesRequest dateAndTimes) {

        List<BookingEntity> entityList = bookingRepository.findFree(Date.valueOf(dateAndTimes.getDate()),
                dateAndTimes.getStartTime(), dateAndTimes.getEndTime());

        Map<String, BookingFreeDTO> result = new HashMap<>(3);
        for (BookingEntity entity : entityList) {
            result.put(entity.getLocation().getName(), BookingMapper.getFreeDTOFromEntity(entity));
        }

        return result;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<String, BookingFreeAllDayDTO> getFreeBookingForAllDay(LocalDate date) {
        List<BookingEntity> entityList = bookingRepository.findFree(Date.valueOf(date));

        Map<LocationEntity, Integer> checkMap = new HashMap();
        Map<String, BookingFreeAllDayDTO> map = new HashMap<>();
        for (BookingEntity entity : entityList) {
            LocationEntity locationEntity = entity.getLocation();

            if (checkMap.containsKey(locationEntity)) {
                map.put(locationEntity.getName(), BookingMapper.get)
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<BookingSimpleDTO> getBookingsByClient(Long vkId) {
        ClientEntity clientEntity = clientService.getEntityByVkId(vkId);

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

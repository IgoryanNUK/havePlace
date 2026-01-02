package vk.haveplace.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.mappers.LocationMapper;
import vk.haveplace.services.objects.*;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingFreeDTO;
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

        List<LocationDateAndTimesDTO> entityList = bookingRepository.findFreeTimeSlotsWithLocationUntil(Date.valueOf(endDate));

        Map<LocalDate, List<TimeSlot>> map = new TreeMap<>();
        Map<LocalDate, Set<Integer>> slotsToLocations = new TreeMap<>();
        for (LocationDateAndTimesDTO dto : entityList) {
            TimeSlot key = new TimeSlot(dto.getStartTime(), dto.getEndTime());
            TimeSlot timeSlot = new TimeSlotWithPrice(
                    key, priceService.getPrice(dto)
            );

            LocalDate date = dto.getDate().toLocalDate();
            if (map.containsKey(date)) {
                List<TimeSlot> list = map.get(date);

                if (!list.contains(timeSlot)) {
                    list.add(timeSlot);
                }

                //добавления слота на весь день
                Set<Integer> idSet = slotsToLocations.get(date);
                if (idSet.contains(dto.getLocation().getId())) {
                    timeSlot = TimeSlot.unite(list.getFirst(), list.getLast());
                    timeSlot = new TimeSlotWithPrice(timeSlot, priceService.getPrice(Date.valueOf(date), timeSlot));

                    if (!list.contains(timeSlot)) {
                        list.add(timeSlot);
                    }
                } else {
                    idSet.add(dto.getLocation().getId());
                }
            } else {
                List<TimeSlot> list = new ArrayList<>(3);
                list.add(timeSlot);
                map.put(date, list);

                Set<Integer> idSet = new TreeSet<>();
                idSet.add(dto.getLocation().getId());
                slotsToLocations.put(date, idSet);
            }
        }

        return map;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<String, BookingFreeDTO> getFreeBookings(DateAndTimesRequest dateAndTimes) {

        List<BookingEntity> entityList = bookingRepository.findFree(Date.valueOf(dateAndTimes.getDate()),
                dateAndTimes.getStartTime(), dateAndTimes.getEndTime());

        if (entityList.isEmpty()) {
            return getFreeBookingForAllDay(dateAndTimes.getDate());
        } else {
            Map<String, BookingFreeDTO> result = new HashMap<>(3);
            for (BookingEntity entity : entityList) {
                result.put(entity.getLocation().getName(), BookingMapper.getFreeDTOFromEntity(entity));
            }

            return result;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Map<String, BookingFreeDTO> getFreeBookingForAllDay(LocalDate date) {
        List<BookingEntity> entityList = bookingRepository.findFree(Date.valueOf(date));

        Map<LocationEntity, List<Integer>> checkMap = new HashMap<>();
        Map<String, BookingFreeDTO> map = new HashMap<>();
        for (BookingEntity entity : entityList) {
            LocationEntity locationEntity = entity.getLocation();

            if (checkMap.containsKey(locationEntity)) {
                List<Integer> idList = checkMap.get(locationEntity);
                idList.add(entity.getId());
                map.put(locationEntity.getName(),
                        new BookingFreeDTO(idList, LocationMapper.getDTOFromEntity(locationEntity)));
            } else {
                List<Integer> idList = new ArrayList<>(2);
                idList.add(entity.getId());
                checkMap.put(locationEntity, idList);
            }
        }

        return map;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<BookingDTO> getBookingsByClient(Long vkId) {
        ClientEntity clientEntity = clientService.getEntityByVkId(vkId);

        if (clientEntity == null) {
            return null;
        }

        List<BookingEntity> entityList = bookingRepository.findAllByClientOrderById(clientEntity);

        Map<DateAndLocation, List<BookingEntity>> checkMap = new HashMap<>();
        for (BookingEntity entity : entityList) {
            DateAndLocation key = new DateAndLocation(entity.getDate(), entity.getLocation());
            if (checkMap.containsKey(key)) {
                checkMap.get(key).add(entity);
            } else {
                List<BookingEntity> list = new ArrayList<>(2);
                list.add(entity);
                checkMap.put(key, list);
            }
        }

        List<BookingDTO> result = new ArrayList<>(checkMap.size());
        for (List<BookingEntity> bookingList : checkMap.values()) {
            result.add(BookingMapper.getDTOFromEntity(bookingList));
        }

        return result;
    }
}

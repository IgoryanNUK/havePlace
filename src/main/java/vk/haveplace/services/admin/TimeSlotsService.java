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
import vk.haveplace.services.objects.ConflictResponse;
import vk.haveplace.services.objects.TimeSlot;
import vk.haveplace.services.objects.TimeSlotResponse;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        if (startDate.isAfter(endDate)) {
            return 0;
        }

        List<RegularEventEntity> regEvents = regularEventRepository.findAll();

        int totalCount = startDate.plusDays(1).datesUntil(endDate.plusDays(1))
                .mapToInt(d -> setTimeSlotsForDay(d, timeMap,
                        regEvents.stream().filter(e -> e.getDayOfWeek().equals(d.getDayOfWeek().toString()) &&
                                d.isAfter(e.getStartDate().toLocalDate().minusDays(1)) &&
                                d.isBefore(e.getEndDate().toLocalDate().plusDays(1))).toList()))
                .sum();

        return totalCount;
    }

    private int setTimeSlotsForDay(LocalDate d, Map<String, List<TimeSlot>> timeMap,
                                   List<RegularEventEntity> regEvents) {
        int totalCount = 0;

        for(TimeSlot timeSlot : timeMap.get(d.getDayOfWeek().toString())) {
            for(LocationEntity location : locationRepository.findAllByIsExistingTrue()) {
                RegularEventEntity entity = regEvents.stream()
                        .filter(e -> e.getLocation().equals(location)
                                && ( (e.getStartTime().equals(timeSlot.getStart())
                                && e.getEndTime().compareTo(timeSlot.getEnd()) >= 0)
                                || (e.getStartTime().compareTo(timeSlot.getStart()) <= 0
                                && e.getEndTime().equals(timeSlot.getEnd()) ) ) )
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
            slot.setDevice("[AUTOMATICALLY]");
            slot.setIsAvailable(false);
            slot.setRegEvent(regEvent);
        }

        bookingRepository.save(slot);

        return 1;
    }

    /**
     * Добавляет таймслоты для дат в периоде, если для какого-то слота или локации
     * записи еще не были созданы (дозаполнение существующих дат новыми локациями/слотами).
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int addMissingTimeSlotsForPeriod(LocalDate endDate, Map<String, List<TimeSlot>> timeMap) {
        LocalDate startDate;
        try {
            // Берем либо сегодняшний день, либо самую раннюю/позднюю дату, с которой хотим начать дозаполнение.
            // Для безопасности и логики дозаполнения логично начать с LocalDate.now()
            // или с первой даты, где потенциально могут быть пропуски.
            startDate = LocalDate.now();
        } catch (Exception e) {
            startDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            return 0;
        }

        List<RegularEventEntity> regEvents = regularEventRepository.findAll();
        // Загружаем только существующие (активные) локации
        List<LocationEntity> activeLocations = locationRepository.findAllByIsExistingTrue();

        int totalCount = startDate.datesUntil(endDate.plusDays(1))
                .mapToInt(d -> addMissingTimeSlotsForDay(d, timeMap, activeLocations,
                        regEvents.stream().filter(e -> e.getDayOfWeek().equals(d.getDayOfWeek().toString()) &&
                                d.isAfter(e.getStartDate().toLocalDate().minusDays(1)) &&
                                d.isBefore(e.getEndDate().toLocalDate().plusDays(1))).toList()))
                .sum();

        return totalCount;
    }

    private int addMissingTimeSlotsForDay(LocalDate d, Map<String, List<TimeSlot>> timeMap,
                                          List<LocationEntity> activeLocations,
                                          List<RegularEventEntity> regEvents) {
        List<TimeSlot> slotsForDay = timeMap.get(d.getDayOfWeek().toString());
        if (slotsForDay == null || slotsForDay.isEmpty()) {
            return 0;
        }

        int totalCount = 0;
        Date sqlDate = Date.valueOf(d);

        // Получаем все БРОНИРОВАНИЯ на этот день одним запросом, чтобы не спамить БД в цикле
        List<BookingEntity> existingBookingsForDay = bookingRepository.findAllByDate(sqlDate);

        for (TimeSlot timeSlot : slotsForDay) {
            for (LocationEntity location : activeLocations) {

                // Проверяем, существует ли уже слот для этой локации в это время
                boolean alreadyExists = existingBookingsForDay.stream()
                        .anyMatch(b -> b.getLocation().equals(location)
                                && b.getStartTime().equals(timeSlot.getStart())
                                && b.getEndTime().equals(timeSlot.getEnd()));

                // Если слот уже есть, просто пропускаем эту итерацию
                if (alreadyExists) {
                    continue;
                }

                // Если слота нет — ищем, не попадает ли он под регулярное событие
                RegularEventEntity entity = regEvents.stream()
                        .filter(e -> e.getLocation().equals(location)
                                && ( (e.getStartTime().equals(timeSlot.getStart())
                                && e.getEndTime().compareTo(timeSlot.getEnd()) >= 0)
                                || (e.getStartTime().compareTo(timeSlot.getStart()) <= 0
                                && e.getEndTime().equals(timeSlot.getEnd()) ) ) )
                        .findFirst().orElse(null);

                // Вызываем ваш существующий метод (он помечен как REQUIRES_NEW, что изолирует транзакции)
                totalCount += setNewTimeSlot(sqlDate, timeSlot, location, entity);
            }
        }

        return totalCount;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer removeAllTimeSlotsForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Integer> idsToDelete = bookingRepository.findAllFromStartDateToEndDateOrderByDate(
                Date.valueOf(startDate), Date.valueOf(endDate)
        ).stream().map(BookingEntity::getId).toList();

        return bookingRepository.deleteAllByIdIn(idsToDelete);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer removeTimeSlotsForDay(LocalDate date,
                                         List<TimeSlot> timeSlots) {
        List<BookingEntity> existingBookingsForDay = bookingRepository.findAllByDate(Date.valueOf(date));

        List<Integer> idsToDelete = new ArrayList<>();
        for (TimeSlot timeSlot : timeSlots) {
            idsToDelete.addAll(existingBookingsForDay.stream()
                    .filter(e ->
                            e.getStartTime().equals(timeSlot.getStart())
                            && e.getEndTime().equals(timeSlot.getEnd())
                    )
                    .map(BookingEntity::getId)
                    .toList());
        }

        return bookingRepository.deleteAllByIdIn(idsToDelete);
    }

    /**
     * Добавляет временные слоты в конкретный день.
     * Пропускает временной слот и возвращает конфликтный ответ, если есть хотя бы одна бронь с наложением времени.
     *
     * @param date
     * @param timeSlots
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer addTimeSlotsForDay(LocalDate date, List<TimeSlot> timeSlots) {
        Date sqlDate = Date.valueOf(date);

        int createdCount = 0;
        ConflictResponse<TimeSlot> conflictResponse = null;

        List<BookingEntity> existingBookingsForDay = bookingRepository.findAllByDate(sqlDate);

        Set<LocationEntity> locations = existingBookingsForDay.stream()
                .map(BookingEntity::getLocation)
                .collect(Collectors.toSet());


        for (TimeSlot timeSlot : timeSlots) {
            boolean isExists = existingBookingsForDay.stream()
                    .anyMatch(e ->
                            (e.getStartTime().compareTo(timeSlot.getStart()) <= 0
                            && e.getEndTime().compareTo(timeSlot.getStart()) > 0)
                            || (e.getStartTime().compareTo(timeSlot.getEnd()) < 0
                            && e.getEndTime().compareTo(timeSlot.getEnd()) >= 0)
                    );

            // conflict processing
            if (isExists) {
                if (conflictResponse == null) {
                    conflictResponse = new ConflictResponse<>();
                }

                conflictResponse.addConflict(timeSlot);

                continue;
            }

            for (LocationEntity location : locations) {
                createdCount += setNewTimeSlot(sqlDate, timeSlot, location, null);
            }
        }

        return createdCount;
    }
}

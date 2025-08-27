package vk.haveplace.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.AdminRepository;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.*;
import vk.haveplace.exceptions.AdminNotFound;
import vk.haveplace.exceptions.BookingNotFound;
import vk.haveplace.exceptions.BookingUpdateError;
import vk.haveplace.services.EventService;
import vk.haveplace.services.mappers.ClientMapper;
import vk.haveplace.services.objects.dto.RegularEventDTO;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class AdminBookingWriteService {

    private final BookingRepository bookingRepository;
    private final AdminRepository adminRepository;
    private final EventService eventService;
    private final RegularEventService regularEventService;

    public AdminBookingWriteService(BookingRepository bookingRepository,
                                    AdminRepository adminRepository, EventService eventService,
                                    RegularEventService regularEventService) {
        this.bookingRepository = bookingRepository;
        this.adminRepository = adminRepository;
        this.eventService = eventService;
        this.regularEventService = regularEventService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean confirmBooking(int bookingId, long adminVkId) {
        BookingEntity entity = bookingRepository.findFirstById(bookingId).orElseThrow();

        if (entity.getStatus().equals(BookingStatus.NEW)) {
            entity.setStatus(BookingStatus.CONFIRMED);

            AdminEntity admin = adminRepository.findByVkId(adminVkId).orElseThrow(() -> new AdminNotFound("vkId = " + adminVkId));

            try {
                eventService.bookingEvent(entity, admin, OperationType.CONFIRM, null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        } else {
            throw new BookingUpdateError(0);
        }

        return true;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean lock(int bookingId, long adminVkId) {
        BookingEntity entity = bookingRepository.findFirstById(bookingId).orElseThrow(() -> new BookingNotFound("id = "+bookingId));

        entity.setIsAvailable(!entity.getIsAvailable());
        entity.setStatus(entity.getIsAvailable() ? BookingStatus.FREE : BookingStatus.LOCKED);

        AdminEntity admin = adminRepository.findByVkId(adminVkId).orElseThrow(() -> new AdminNotFound("vkId = " + adminVkId));

        try {
            eventService.bookingEvent(entity, admin, OperationType.LOCK, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return entity.getIsAvailable();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean setAdminShift(Integer adminId, DateAndTimesRequest dateAndTime) {
        AdminEntity admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFound("id = " + adminId));

        List<BookingEntity> bookingList = bookingRepository.findAllByDateAndStartTimeAndEndTime(
                Date.valueOf(dateAndTime.getDate()), dateAndTime.getStartTime(), dateAndTime.getEndTime()
        );

        int k = 0;
        for (BookingEntity booking : bookingList) {
            booking.setAdmin(admin);
            bookingRepository.saveAndFlush(booking);
            k++;
        }

        return k > 0;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer bookRegularEvent(int regEventId) {
        RegularEventDTO regEventDTO = regularEventService.getById(regEventId);
        BookingEntity booking = bookingRepository
                .findFirstByOrderByDateDesc().orElseGet(() -> null);
        LocalDate endDate;
        if (booking == null) {
            return 0;
        } else {
            endDate = booking.getDate().toLocalDate();
        }

        List<LocalDate> dates = LocalDate.now().plusDays(1).datesUntil(endDate.plusDays(1))
                .filter(e -> e.getDayOfWeek().toString().equals(regEventDTO.getDayOfWeek())).toList();

        ClientEntity client = ClientMapper.getEntityFromDTO(regEventDTO.getClient());
        Integer numberOfPlayers = regEventDTO.getNumberOfPlayers();
        String regEventName = regEventDTO.getName();

        AtomicInteger failsCount = new AtomicInteger();
        for (LocalDate date : dates) {
            // проверяет по локации, времени(учитывая брони на весь день)
            List<Integer> idList = bookingRepository.findAllByDate(Date.valueOf(date)).stream().filter(e -> e.getLocation().getId() == regEventDTO.getLocation().getId()
                    && (e.getStartTime().equals(regEventDTO.getStartTime()) && e.getEndTime().compareTo(regEventDTO.getEndTime()) <=0
                    || e.getStartTime().compareTo(regEventDTO.getStartTime()) >= 0 && e.getEndTime().equals(regEventDTO.getEndTime()))).map(BookingEntity::getId).toList();

            for (Integer id : idList) {
                int s = bookingRepository.saveNew(id, client, "AUTOMATICALLY", numberOfPlayers, regEventName, BookingStatus.CONFIRMED);
                if (s != 1) {
                    failsCount.getAndIncrement();
                }

                try {
                    BookingEntity ent = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFound("id = " + id));
                    eventService.bookingEvent(ent, ent.getClient(), OperationType.BOOK, null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return failsCount.get();
    }
}

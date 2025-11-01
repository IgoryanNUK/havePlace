package vk.haveplace.services.admin;

import jakarta.persistence.EntityManager;
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
import vk.haveplace.services.ClientService;
import vk.haveplace.services.EventService;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.mappers.ClientMapper;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.RegularEventDTO;
import vk.haveplace.services.objects.requests.AdminBookingRequest;
import vk.haveplace.services.objects.requests.BookingRequest;
import vk.haveplace.services.objects.requests.ClientRequest;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class AdminBookingWriteService {

    private final BookingRepository bookingRepository;
    private final AdminRepository adminRepository;
    private final EventService eventService;
    private final RegularEventService regularEventService;
    private final ClientService clientService;
    private final EntityManager entityManager;
    private final AdminService adminService;

    public AdminBookingWriteService(BookingRepository bookingRepository,
                                    AdminRepository adminRepository, EventService eventService,
                                    RegularEventService regularEventService,
                                    ClientService clientService,
                                    EntityManager entityManager,
                                    AdminService adminService) {
        this.bookingRepository = bookingRepository;
        this.adminRepository = adminRepository;
        this.eventService = eventService;
        this.regularEventService = regularEventService;
        this.clientService = clientService;
        this.entityManager = entityManager;
        this.adminService = adminService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean confirmBooking(int bookingId, long adminVkId) {
        BookingEntity entity = bookingRepository.findFirstById(bookingId).orElseThrow();

        if (entity.getStatus().equals(BookingStatus.NEW)) {
            entity.setStatus(BookingStatus.CONFIRMED);

            AdminEntity admin = adminService.getEntityByVkId(adminVkId);

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

        AdminEntity admin = adminService.getEntityByVkId(adminVkId);

        try {
            eventService.bookingEvent(entity, admin, OperationType.LOCK, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return entity.getIsAvailable();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean setAdminShift(Long adminId, DateAndTimesRequest dateAndTime) {
        AdminEntity admin = adminService.getEntityByVkId(adminId);

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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO update(AdminBookingRequest booking) {
        ClientRequest client = booking.getClient();
        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        List<Integer> idList = booking.getIdList();

        int updated = 0;
        for (Integer id : idList) {
            updated += bookingRepository.update(id, clientEntity, booking.getDevice(),
                    booking.getNumberOfPlayers(), booking.getComments(), BookingStatus.NEW);
        }


        if (updated == booking.getIdList().size()) {
            entityManager.clear();

            List<BookingEntity> entityList = new ArrayList<>(2);
            for (Integer id : idList) {
                BookingEntity entity = bookingRepository.findFirstById(id).orElseThrow();
                entityList.add(entity);

                try {
                    AdminEntity admin = adminService.getEntityByVkId(booking.getAdminVkId());
                    eventService.bookingEvent(entity, clientEntity, admin, OperationType.UPDATE, null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return BookingMapper.getDTOFromEntity(entityList);
        } else {
            throw new BookingUpdateError(updated);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean remove(List<Integer> idList, ClientRequest client, Long adminVkId) {;
        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        int updated = 0;
        for (Integer id : idList) {
            updated += bookingRepository.cancel(id, clientEntity);
        }

        if (updated == idList.size()) {
            for (Integer id : idList) {

                try {
                    AdminEntity admin = adminService.getEntityByVkId(adminVkId);
                    eventService.bookingEvent(bookingRepository.findById(id).orElseThrow(() -> new BookingNotFound("id = " + id)),
                            clientEntity, admin, OperationType.CANCEL, null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return true;
        } else {
            throw new BookingUpdateError(updated);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO book(AdminBookingRequest booking) {
        List<Integer> idList = booking.getIdList();

        ClientRequest client = booking.getClient();
        ClientEntity clientEntity = clientService.getEntityByRequest(client);
        int updated = 0;
        for (Integer id : idList) {
            updated += bookingRepository.saveNew(id, clientEntity, booking.getDevice(),
                    booking.getNumberOfPlayers(), booking.getComments(), BookingStatus.NEW);
        }

        if (updated == idList.size()) {
            entityManager.clear();
            List<BookingEntity> entityList = new ArrayList<>(2);
            for (Integer id : idList) {
                BookingEntity entity = bookingRepository.findFirstById(id).orElseThrow(() -> new BookingNotFound("id = " + id));
                entityList.add(entity);

                try {
                    AdminEntity admin = adminService.getEntityByVkId(booking.getAdminVkId());
                    eventService.bookingEvent(entity, entity.getClient(), admin, OperationType.BOOK, null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return BookingMapper.getDTOFromEntity(entityList);
        } else {
            throw new BookingUpdateError(updated);
        }
    }
}

package vk.haveplace.services.admin;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.*;
import vk.haveplace.exceptions.BookingNotFound;
import vk.haveplace.exceptions.BookingUpdateError;
import vk.haveplace.exceptions.RegularEventBusy;
import vk.haveplace.services.ClientService;
import vk.haveplace.services.EventService;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.mappers.RegularEventMapper;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.dto.BookingsRegularEventDto;
import vk.haveplace.services.objects.requests.AdminBookingRequest;
import vk.haveplace.services.objects.requests.ClientRequest;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;
import vk.haveplace.services.objects.requests.RegularEventRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdminBookingWriteService {

    private final BookingRepository bookingRepository;
    private final EventService eventService;
    private final ClientService clientService;
    private final EntityManager entityManager;
    private final AdminService adminService;
    private final AdminBookingReadService readService;

    public AdminBookingWriteService(BookingRepository bookingRepository,
                                    EventService eventService,
                                    ClientService clientService,
                                    EntityManager entityManager,
                                    AdminService adminService,
                                    AdminBookingReadService readService) {
        this.bookingRepository = bookingRepository;
        this.eventService = eventService;
        this.clientService = clientService;
        this.entityManager = entityManager;
        this.adminService = adminService;
        this.readService = readService;
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
    public void bookRegularEvent(RegularEventEntity entity, List<Integer> ids, Long adminVkId) {
        ClientEntity client = entity.getClient();
        Integer numberOfPlayers = entity.getNumberOfPlayers();
        String regEventName = entity.getName();
        AdminEntity admin = adminService.getEntityByVkId(adminVkId);

        for (Integer id : ids) {
            int s = bookingRepository.saveNew(id, client, "AUTOMATICALLY", numberOfPlayers, regEventName, BookingStatus.CONFIRMED, entity);
            if (s != 1) {
                throw new RegularEventBusy();
            }

            try {
                BookingEntity ent = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFound("id = " + id));
                eventService.bookingEvent(ent, ent.getClient(), admin, OperationType.BOOK, null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int deleteRegularEvent(RegularEventEntity entity) {
        return bookingRepository.cancelReqularEventFrom(entity, Date.valueOf(LocalDate.now()));
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

package vk.haveplace.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.BookingStatus;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.database.entities.OperationType;
import vk.haveplace.exceptions.BookingNotFound;
import vk.haveplace.exceptions.BookingUpdateError;
import vk.haveplace.exceptions.WrongClient;
import vk.haveplace.services.mappers.BookingMapper;
import vk.haveplace.services.objects.dto.BookingDTO;
import vk.haveplace.services.objects.requests.BookingRequest;
import vk.haveplace.services.objects.requests.ClientRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientBookingWriteService {

    private final ClientService clientService;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final EventService eventService;

    @Autowired
    public ClientBookingWriteService(ClientService clientService, BookingRepository bookingRepository,
                                     EntityManager entityManager, EventService eventService) {
        this.clientService = clientService;
        this.bookingRepository = bookingRepository;
        this.entityManager = entityManager;
        this.eventService = eventService;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO book(BookingRequest booking) {
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
                eventService.bookingEvent(entity, clientEntity, OperationType.BOOK, null);
            }
            return BookingMapper.getDTOFromEntity(entityList);
        } else {
            throw new BookingUpdateError(updated);
        }
    }

    /**
     * После обновления статус бронирования меняется на NEW.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDTO update(BookingRequest booking) {
        ClientRequest client = booking.getClient();
        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        List<Integer> idList = booking.getIdList();

        int updated = 0;
        for (Integer id : idList) {
            validateAccess(clientEntity, id);
            updated = bookingRepository.update(id, clientEntity, booking.getDevice(),
                    booking.getNumberOfPlayers(), booking.getComments(), BookingStatus.NEW);
        }


        if (updated == booking.getIdList().size()) {
            entityManager.clear();

            List<BookingEntity> entityList = new ArrayList<>(2);
            for (Integer id : idList) {
                BookingEntity entity = bookingRepository.findFirstById(id).orElseThrow();
                entityList.add(entity);
                eventService.bookingEvent(entity, clientEntity, OperationType.UPDATE, null);
            }
            return BookingMapper.getDTOFromEntity(entityList);
        } else {
            throw new BookingUpdateError(updated);
        }
    }

    private void validateAccess(ClientEntity client, int bookingId) {
        ClientEntity databaseClient = bookingRepository.findFirstById(bookingId).orElseThrow().getClient();

        if (databaseClient == null) {
            throw new BookingNotFound("id = " +  bookingId);
        }
        if (!databaseClient.equals(client)) {
            throw new WrongClient(client.getName());
        }
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean remove(List<Integer> idList, ClientRequest client) {;
        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        int updated = 0;
        for (Integer id : idList) {
            validateAccess(clientEntity, id);

            updated += bookingRepository.cancel(id, clientEntity);
        }

        if (updated == idList.size()) {
            for (Integer id : idList) {
                eventService.bookingEvent(bookingRepository.findById(id).orElseThrow(() -> new BookingNotFound("id = " + id)),
                        clientEntity, OperationType.CANCEL, null);
            }
            return true;
        } else {
            throw new BookingUpdateError(updated);
        }
    }
}

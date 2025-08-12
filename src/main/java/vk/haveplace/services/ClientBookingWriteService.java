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
import vk.haveplace.services.objects.dto.BookingSimpleDTO;
import vk.haveplace.services.objects.requests.BookingRequest;
import vk.haveplace.services.objects.requests.ClientRequest;

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

    private BookingSimpleDTO checkUpdateResult(int updated, int bookingId) {
        if (updated == 1) {
            entityManager.clear();
            BookingEntity entity = bookingRepository.findFirstById(bookingId).orElseThrow();
            return BookingMapper.getSimpleDTOFromEntity(entity);
        } else {
            throw new BookingUpdateError(updated);
        }
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingSimpleDTO setNewBooking(BookingRequest booking) {
        ClientRequest client = booking.getClient();

        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        int updated = bookingRepository.saveNew(booking.getId(), clientEntity, booking.getDevice(),
                booking.getNumberOfPlayers(), booking.getComments(), BookingStatus.NEW);

        if (updated == 1) {
            entityManager.clear();
            BookingEntity entity = bookingRepository.findFirstById(booking.getId()).orElseThrow();

            eventService.bookingEvent(entity, clientEntity, OperationType.BOOK, null);
            return BookingMapper.getSimpleDTOFromEntity(entity);
        } else {
            throw new BookingUpdateError(updated);
        }
    }

    /**
     * После обновления статус бронирования меняется на NEW.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingSimpleDTO update(BookingRequest booking) {
        ClientRequest client = booking.getClient();
        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        validateAccess(clientEntity, booking.getId());

        int updated = bookingRepository.update(booking.getId(), clientEntity, booking.getDevice(),
                booking.getNumberOfPlayers(), booking.getComments(), BookingStatus.NEW);

        if (updated == 1) {
            entityManager.clear();
            BookingEntity entity = bookingRepository.findFirstById(booking.getId()).orElseThrow();

            eventService.bookingEvent(entity, clientEntity, OperationType.UPDATE, null);
            return BookingMapper.getSimpleDTOFromEntity(entity);
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
    public boolean remove(int id, ClientRequest client) {;
        ClientEntity clientEntity = clientService.getEntityByRequest(client);

        validateAccess(clientEntity, id);

        int updated = bookingRepository.cancel(id, clientEntity);

        if (updated == 1) {
            eventService.bookingEvent(bookingRepository.findById(id).orElseThrow(() -> new BookingNotFound("id = " + id)),
                    clientEntity, OperationType.CANCEL, null);
            return true;
        } else {
            throw new BookingUpdateError(updated);
        }
    }
}

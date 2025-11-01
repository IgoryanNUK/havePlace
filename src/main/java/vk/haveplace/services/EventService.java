package vk.haveplace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.EventRepository;
import vk.haveplace.database.entities.*;

@Service
public class EventService {

    private EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE,
    propagation = Propagation.REQUIRES_NEW)
    public void bookingEvent(BookingEntity booking, ClientEntity client, OperationType operationType, String comments) {
        EventEntity event = new EventEntity(booking, client, operationType, comments);

        eventRepository.saveAndFlush(event);
    }


    @Transactional(isolation = Isolation.SERIALIZABLE,
    propagation = Propagation.REQUIRES_NEW)
    public EventEntity bookingEvent(BookingEntity booking, AdminEntity admin, OperationType operationType, String comments) {
        EventEntity event = new EventEntity(booking, admin, operationType, comments);

        return eventRepository.saveAndFlush(event);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRES_NEW)
    public void bookingEvent(BookingEntity booking, ClientEntity client, AdminEntity admin, OperationType operationType, String comments) {
        EventEntity event = new EventEntity(booking, client, admin, operationType, comments);

        eventRepository.saveAndFlush(event);
    }
}

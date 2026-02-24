package vk.haveplace.services.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.LocationRepository;
import vk.haveplace.database.RegularEventRepository;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.database.entities.RegularEventEntity;
import vk.haveplace.exceptions.RegularEventBusy;
import vk.haveplace.exceptions.RegularEventNotFound;
import vk.haveplace.services.ClientService;
import vk.haveplace.services.mappers.RegularEventMapper;
import vk.haveplace.services.objects.dto.BookingsRegularEventDto;
import vk.haveplace.services.objects.dto.RegularEventDTO;
import vk.haveplace.services.objects.requests.RegularEventRequest;
import vk.haveplace.services.objects.requests.RegularEventUpdateRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegularEventService {

    private final LocationRepository locationRepository;
    private final ClientService clientService;
    private final RegularEventRepository repository;
    private final AdminBookingWriteService bookingWriteService;
    private final AdminBookingReadService bookingReadService;

    public RegularEventService(LocationRepository locationRepository, ClientService clientService,
                               RegularEventRepository repository,
                               AdminBookingWriteService bookingWriteService,
                               AdminBookingReadService bookingReadService) {
        this.locationRepository = locationRepository;
        this.clientService = clientService;
        this.repository = repository;
        this.bookingWriteService = bookingWriteService;
        this.bookingReadService = bookingReadService;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingsRegularEventDto add(RegularEventRequest request) {
        LocationEntity locationEntity = locationRepository.findById(request.getLocationId()).orElseThrow();

        ClientEntity clientEntity = clientService.getEntityByRequest(request.getClient());
        RegularEventEntity entity = RegularEventMapper.getEntityFromRequest(request, clientEntity, locationEntity);

        repository.saveAndFlush(entity);

        BookingsRegularEventDto check = bookingReadService.checkRegularEventBookings(request);
        bookingWriteService.bookRegularEvent(entity, check.getOk(), request.getAdminVkId());

        return check;
    }

    private void validateRequest(RegularEventRequest request, LocationEntity location) {
        if (repository.findFirstByLocationAndStartTimeAndEndTimeAndDayOfWeek(location, request.getStartTime(), request.getEndTime(),
                request.getDayOfWeek()).isPresent()) {
            throw new RegularEventBusy();
        }
    }

    private void validateRequest(RegularEventUpdateRequest request, LocationEntity location) {
        Optional<RegularEventEntity> opt = repository.findFirstByLocationAndStartTimeAndEndTimeAndDayOfWeek(location, request.getStartTime(), request.getEndTime(),
                request.getDayOfWeek());

        if (opt.isPresent()) {
            if (!opt.get().getId().equals(request.getId())){
                throw new RegularEventBusy();
            }
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingsRegularEventDto update(RegularEventUpdateRequest request) {
        LocationEntity locationEntity = locationRepository.findById(request.getLocationId()).orElseThrow();

        ClientEntity clientEntity = clientService.getEntityByRequest(request.getClient());
        RegularEventEntity entity = RegularEventMapper.getEntityFromRequest(request, clientEntity, locationEntity);
        repository.save(entity);

        RegularEventEntity old = repository.findById(request.getId())
                .orElseThrow(() -> new RegularEventNotFound("id = " + request.getId()));
        bookingWriteService.cancelRegularEventFrom(old, Date.valueOf(LocalDate.now()));

        BookingsRegularEventDto check = bookingReadService.checkRegularEventBookings(request);
        bookingWriteService.bookRegularEvent(entity, check.getOk(), request.getAdminVkId());

        return check;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int remove(int id) {
        RegularEventEntity entity = repository.findById(id).orElse(null);
        if (entity != null) {
            int canceled = bookingWriteService.cancelRegularEventFrom(entity, entity.getStartDate());
            repository.delete(entity);
            return canceled;
        } else {
            return -1;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<RegularEventDTO> getAll() {
        List<RegularEventEntity> entityList = repository.findAll();

        List<RegularEventDTO> list = new ArrayList<>(entityList.size());
        for (RegularEventEntity entity : entityList) {
            list.add(RegularEventMapper.getDTOFromEntity(entity));
        }

        return list;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public RegularEventDTO getById(int id) {
        RegularEventEntity entity = repository.findById(id).orElseThrow(() -> new RegularEventNotFound("id = " + id));
        return RegularEventMapper.getDTOFromEntity(entity);
    }
}

package vk.haveplace.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.PricesRepository;
import vk.haveplace.database.entities.PriceEntity;
import vk.haveplace.exceptions.PriceNotFound;
import vk.haveplace.services.objects.LocationDateAndTimesDTO;
import vk.haveplace.services.objects.TimeSlot;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {


    private final PricesRepository pricesRepository;

    public PriceService(PricesRepository pricesRepository) {
        this.pricesRepository = pricesRepository;
    }

    public Integer getPrice(LocationDateAndTimesDTO dto) {
        return getPrice(dto.getDate(), dto.getStartTime(), dto.getEndTime());
    }

    public Integer getPrice(Date date, TimeSlot timeSlot) {
        return getPrice(date, timeSlot.getStart(), timeSlot.getEnd());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW)
    public Integer getPrice(Date date, Time startTime, Time endTime) {
        return pricesRepository.getPriceByDateAndTimeAndDayType(
                date,
                startTime,
                endTime
        ).orElseThrow(() -> new PriceNotFound(
                "date: %s, start time: %s, end time: %s".formatted(date, startTime, endTime)
        ));
    }
}

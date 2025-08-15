package vk.haveplace.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.PricesRepository;
import vk.haveplace.database.entities.PriceEntity;
import vk.haveplace.services.objects.TimeSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {


    private final PricesRepository pricesRepository;

    public PriceService(PricesRepository pricesRepository) {
        this.pricesRepository = pricesRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,
    propagation = Propagation.REQUIRES_NEW)
    public Map<TimeSlot, Integer> getPriceMap() {
        List<PriceEntity> list = pricesRepository.findAll();

        Map<TimeSlot, Integer> map = new HashMap<>();
        for (PriceEntity entity : list) {
            TimeSlot timeSlot = new TimeSlot(entity.getStart(), entity.getEnd());

            map.put(timeSlot, entity.getPrice());
        }

        return map;
    }
}

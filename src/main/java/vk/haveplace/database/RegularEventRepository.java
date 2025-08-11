package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vk.haveplace.database.entities.LocationEntity;
import vk.haveplace.database.entities.RegularEventEntity;

import java.sql.Time;
import java.util.Optional;

@Repository
public interface RegularEventRepository extends JpaRepository<RegularEventEntity, Integer> {
    Optional<RegularEventEntity> findFirstByLocationAndStartTimeAndEndTimeAndDayOfWeek(LocationEntity location,
                                                                                       Time startTime,
                                                                                       Time endTime,
                                                                                       String dayOfWeek);
}

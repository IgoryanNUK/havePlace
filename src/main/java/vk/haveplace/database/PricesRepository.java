package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vk.haveplace.database.entities.PriceEntity;

import java.sql.Time;
import java.util.Date;
import java.util.Optional;

@Repository
public interface PricesRepository extends JpaRepository<PriceEntity, Long> {
    @Query(
           "select p.price " +
                   "FROM PriceEntity p " +
                   "WHERE (p.startDate IS NULL OR p.startDate <= :date) AND (p.endDate IS NULL OR p.endDate >= :date) " +
                   "AND p.startTime = :startTime AND p.endTime = :endTime"
    )
    Optional<Integer> getPriceByDateAndTimeAndDayType(
            @Param("date") Date date,
            @Param("startTime") Time startTime,
            @Param("endTime") Time endTime
    );
}

package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.entities.EventEntity;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Transactional
    void deleteByBookingId(Integer bookingId);

    @Transactional
    @Modifying
    @Query("delete from EventEntity e where e.booking.id in :bookingIds")
    void deleteByBookingIds(@Param("bookingIds") List<Integer> bookingIds);
}

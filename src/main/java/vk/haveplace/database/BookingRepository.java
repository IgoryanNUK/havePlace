package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.BookingStatus;
import vk.haveplace.database.entities.ClientEntity;
import vk.haveplace.services.objects.DateAndTimesDTO;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
    @Modifying(flushAutomatically = true)
    @Query("update BookingEntity b set " +
            "b.client = ?2, " +
            "b.device = ?3, " +
            "b.numberOfPlayers = ?4, " +
            "b.comments = ?5, " +
            "b.status = ?6, " +
            "b.isAvailable = false " +
            "WHERE b.id = ?1 and b.isAvailable = true")
    int saveNew(int bookingId, ClientEntity client, String device, int numberOfPlayers, String comments, BookingStatus status);

    @Modifying(flushAutomatically = true)
    @Query(
            "update BookingEntity b set "  +
                    "b.device = ?3, " +
                    "b.numberOfPlayers = ?4, " +
                    "b.comments = ?5, " +
                    "b.status = ?6, " +
                    "b.isAvailable = false " +
                    "WHERE b.id = ?1 and b.client = ?2"
    )
    int update(int id, ClientEntity client, String device, int numberOfPlayers, String comments, BookingStatus status);

    @Modifying
    @Query(
            "update BookingEntity b set " +
                    "b.client = null, " +
                    "b.device = null, " +
                    "b.numberOfPlayers = 0, " +
                    "b.comments = null, " +
                    "b.status = 'FREE', " +
                    "b.isAvailable = true " +
                    "WHERE b.id = ?1 and b.client = ?2"
    )
    int cancel(int id, ClientEntity client);


    Optional<BookingEntity> findFirstByOrderByDateDesc();

    Optional<BookingEntity> findFirstById(int id);

    @Query(
            "select distinct new vk.haveplace.services.objects.DateAndTimesDTO(b.date, b.startTime, b.endTime) from BookingEntity b " +
                    "WHERE b.isAvailable = true and b.date < :date"
    )
    List<DateAndTimesDTO> findFreeTimeSlotsUntil(@Param("date") Date endDate);

    @Query(
            "select distinct new vk.haveplace.services.objects.DateAndTimesDTO(b.date, b.startTime, b.endTime) from BookingEntity b " +
                    "WHERE b.date < :date"
    )
    List<DateAndTimesDTO> findTimeSlotsUntil(@Param("date") Date endDate);

    @Query(
            "select b from BookingEntity b " +
                    "WHERE b.date = :date and b.startTime = :startTime and b.endTime = :endTime and b.isAvailable = true"
    )
    List<BookingEntity> findFreeBookingsUntil(Date date, Time startTime, Time endTime);

    List<BookingEntity> findAllByDateAndStartTimeAndEndTime(Date date, Time startTime, Time endTime);

    List<BookingEntity> findAllByClientOrderById(ClientEntity clientEntity);
}

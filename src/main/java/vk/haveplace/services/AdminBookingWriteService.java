package vk.haveplace.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.AdminRepository;
import vk.haveplace.database.BookingRepository;
import vk.haveplace.database.entities.AdminEntity;
import vk.haveplace.database.entities.BookingEntity;
import vk.haveplace.database.entities.BookingStatus;
import vk.haveplace.exceptions.AdminNotFound;
import vk.haveplace.exceptions.BookingUpdateError;
import vk.haveplace.services.objects.dto.AdminDTO;
import vk.haveplace.services.objects.requests.DateAndTimesRequest;

import java.sql.Date;
import java.util.List;

@Service
public class AdminBookingWriteService {

    private final BookingRepository bookingRepository;
    private final AdminRepository adminRepository;

    public AdminBookingWriteService(BookingRepository bookingRepository,
                                    AdminRepository adminRepository) {
        this.bookingRepository = bookingRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean confirmBooking(int bookingId) {
        BookingEntity entity = bookingRepository.findFirstById(bookingId).orElseThrow();

        if (entity.getStatus().equals(BookingStatus.NEW)) {
            entity.setStatus(BookingStatus.CONFIRMED);
        } else {
            throw new BookingUpdateError(0);
        }

        return true;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean lock(int bookingId) {
        BookingEntity entity = bookingRepository.findFirstById(bookingId).orElseThrow();

        entity.setIsAvailable(!entity.getIsAvailable());
        return entity.getIsAvailable();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean setAdminShift(Integer adminId, DateAndTimesRequest dateAndTime) {
        AdminEntity admin = adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFound(adminId));

        List<BookingEntity> bookingList = bookingRepository.findAllByDateAndStartTimeAndEndTime(
                Date.valueOf(dateAndTime.getDate()), dateAndTime.getStartTime(), dateAndTime.getEndTime()
        );

        int k = 0;
        for (BookingEntity booking : bookingList) {
            booking.setAdmin(admin);
            bookingRepository.saveAndFlush(booking);
            k++;
        }

        return k > 0;
    }
}

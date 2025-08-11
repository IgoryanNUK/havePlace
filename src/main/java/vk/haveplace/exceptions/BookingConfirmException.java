package vk.haveplace.exceptions;

import vk.haveplace.database.entities.BookingStatus;

public class BookingConfirmException extends RuntimeException {
    private BookingStatus status;

    public BookingConfirmException(BookingStatus status) {
        super("Для подтверждения запись должна иметь статус NEW, текущий статус: ");
    }

    @Override
    public String getMessage() {
        return super.getMessage() + status + ".";
    }
}

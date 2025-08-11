package vk.haveplace.exceptions;

public class BookingNotFound extends RuntimeException {
    private final String value;

    public BookingNotFound(String value) {
        super("Не найдена запись со значениями: ");
        this.value = value;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + value;
    }

}

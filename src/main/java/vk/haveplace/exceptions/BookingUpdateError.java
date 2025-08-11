package vk.haveplace.exceptions;

public class BookingUpdateError extends RuntimeException {
    private final int updated;

    public BookingUpdateError(int updated) {
        super("");
        this.updated = updated;
    }

    @Override
    public String getMessage() {
        return "Ошибка обновление записи. Обновлено: " + updated + " строк";
    }
}

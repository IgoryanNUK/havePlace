package vk.haveplace.exceptions;

public class AdminNotFound extends RuntimeException {
    private final String value;

    public AdminNotFound(String value) {
        super("Не найден админ с данными: ");
        this.value = value;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + value;
    }
}

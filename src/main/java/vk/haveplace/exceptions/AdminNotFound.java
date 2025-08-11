package vk.haveplace.exceptions;

public class AdminNotFound extends RuntimeException {
    private Integer id;

    public AdminNotFound(Integer id) {
        super("Не найден админ с id ");
        this.id = id;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + id;
    }
}

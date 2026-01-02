package vk.haveplace.exceptions;

public class PriceNotFound extends RuntimeException {
    private final String value;

    public PriceNotFound(String value) {
        super("Не найдена цена соответствующая данным: ");
        this.value = value;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + value;
    }
}

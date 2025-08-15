package vk.haveplace.exceptions;

public class RegularEventNotFound extends RuntimeException {
    private final String value;

    public RegularEventNotFound(String value) {
        super("Не найдено регулярное событие со значением: ");
        this.value = value;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + value;
    }
}

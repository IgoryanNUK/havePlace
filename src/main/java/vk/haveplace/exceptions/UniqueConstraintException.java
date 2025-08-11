package vk.haveplace.exceptions;

public class UniqueConstraintException extends RuntimeException {
    private final String value;

    public UniqueConstraintException(String value) {
        super("Нарушение уникальности значения. В хранилище уже существует запись со значением: ");
        this.value = value;
    }

    @Override
    public String getMessage() {return super.getMessage() + value;}
}

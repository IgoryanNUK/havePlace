package vk.haveplace.exceptions;

public class UnknownException extends RuntimeException {
    private final Exception exception;

    public UnknownException(Exception e) {
        super(e.getMessage());
        this.exception = e;
    }

    @Override
    public String getMessage() {
        return "Возникла неизвестная ошибка на сервере.";
    }

    public Exception getException() {return exception;}
}

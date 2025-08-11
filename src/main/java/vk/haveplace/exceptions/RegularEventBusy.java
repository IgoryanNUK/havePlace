package vk.haveplace.exceptions;

public class RegularEventBusy extends RuntimeException {
    public RegularEventBusy() {
        super("");
    }

    @Override
    public String getMessage() {return "Регулярное событие в этот день недели, время и локации уже существует.";}
}

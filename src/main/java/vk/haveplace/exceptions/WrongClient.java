package vk.haveplace.exceptions;

public class WrongClient extends RuntimeException {
    private final String name;

    public WrongClient(String name) {
        super("");
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Попытка обновления чужой записи пользователем " + name + ".";
    }
}

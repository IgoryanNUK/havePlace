package vk.haveplace.exceptions;

public class TimeSlotConflict extends RuntimeException {
    public TimeSlotConflict(String message) {
        super(message);
    }
}

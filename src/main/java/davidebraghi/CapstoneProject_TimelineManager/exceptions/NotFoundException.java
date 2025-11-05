package davidebraghi.CapstoneProject_TimelineManager.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Long id) {
        super("Record with ID: " + String.valueOf(id) + " has not been found!");
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
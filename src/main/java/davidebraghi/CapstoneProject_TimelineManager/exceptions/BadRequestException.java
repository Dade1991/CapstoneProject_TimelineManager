package davidebraghi.CapstoneProject_TimelineManager.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
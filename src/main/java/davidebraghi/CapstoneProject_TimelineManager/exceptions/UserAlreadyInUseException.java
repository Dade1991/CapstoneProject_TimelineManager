package davidebraghi.CapstoneProject_TimelineManager.exceptions;

public class UserAlreadyInUseException extends RuntimeException {
    public UserAlreadyInUseException(String message) {
        super(message);
    }
}

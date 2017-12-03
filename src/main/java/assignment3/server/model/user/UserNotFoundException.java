package assignment3.server.model.user;

/**
 * Thrown when the username already exists in the current session.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String msg) {
        super(msg);
    }

    public UserNotFoundException(Throwable rootCause) {
        super(rootCause);
    }

    public UserNotFoundException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

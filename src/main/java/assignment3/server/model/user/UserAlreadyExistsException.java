package assignment3.server.model.user;

/**
 * Thrown when the username already exists in the current session.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }

    public UserAlreadyExistsException(Throwable rootCause) {
        super(rootCause);
    }

    public UserAlreadyExistsException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

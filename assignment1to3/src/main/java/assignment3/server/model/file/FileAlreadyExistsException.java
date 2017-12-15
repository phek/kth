package assignment3.server.model.file;

public class FileAlreadyExistsException extends RuntimeException {

    public FileAlreadyExistsException(String msg) {
        super(msg);
    }

    public FileAlreadyExistsException(Throwable rootCause) {
        super(rootCause);
    }

    public FileAlreadyExistsException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}


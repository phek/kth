package assignment3.server.model.file;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String msg) {
        super(msg);
    }

    public FileNotFoundException(Throwable rootCause) {
        super(rootCause);
    }

    public FileNotFoundException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}


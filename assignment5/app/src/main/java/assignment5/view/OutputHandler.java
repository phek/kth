package assignment5.view;

public interface OutputHandler {

    /**
     * Sends a message to an OutputHandler.
     *
     * @param message The message to be sent.
     */
    void sendMessage(String message);
    
    /**
     * Sends a message to an OutputHandler.
     *
     * @param error The error to be sent.
     */
    void sendError(String error);
}

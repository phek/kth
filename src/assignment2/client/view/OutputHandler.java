package assignment2.client.view;

public interface OutputHandler {

    /**
     * Sends a message to an OutputHandler.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(String message);
    
    /**
     * Sends a message to an OutputHandler.
     *
     * @param error The error to be sent.
     */
    public void sendError(String error);
}

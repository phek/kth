package assignment2.shared;

import java.nio.ByteBuffer;

/**
 * Handles messages to send and received.
 */
public class MessageHandler {

    private StringBuilder data;
    private String message;
    private boolean validMessage;

    /**
     * Creates a new MessageHandler.
     */
    public MessageHandler() {
        message = "";
        validMessage = false;
        data = new StringBuilder();
    }

    /**
     * Appends a received data to the current message.
     *
     * @param receivedMessage The message received.
     */
    public synchronized void appendReceivedMessage(String receivedMessage) {
        data.append(receivedMessage);
        extractMessage();
    }

    /**
     * Gets the current message.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Tells if the message is completed or invalid.
     *
     * @return If it's valid or not.
     */
    public boolean isValidMessage() {
        return validMessage;
    }

    /**
     * Removes the current message and starts over with an empty message.
     */
    public void reset() {
        message = "";
        validMessage = false;
        data = new StringBuilder();
    }

    /**
     * Creates a ByteBuffer message with length header.
     *
     * @param message The message.
     * @return The message as a ByteBuffer with a length header.
     */
    public static ByteBuffer createMessage(String message) {
        String msg = message.length() + Command.DELIMITER + message;
        return ByteBuffer.wrap(msg.getBytes());
    }

    private void extractMessage() {
        String chars = data.toString();
        String[] parsedMessage = chars.split(Command.DELIMITER);

        /* Corrupt message */
        if (parsedMessage.length < 2) {
            reset();
            return;
        }

        int messageLength = Integer.parseInt(parsedMessage[0]);
        if (isCompleteMessage(messageLength, parsedMessage[1])) {
            message = parsedMessage[1].substring(0, messageLength);
            validMessage = true;
        }
    }

    private boolean isCompleteMessage(int length, String message) {
        return message.length() >= length;
    }
}

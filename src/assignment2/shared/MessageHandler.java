package assignment2.shared;

import java.nio.ByteBuffer;

public class MessageHandler {

    private StringBuilder data;
    private String message;
    private boolean validMessage;
    
    public MessageHandler() {
        message = "";
        validMessage = false;
        data = new StringBuilder();
    }
    
    public synchronized void appendRecievedMessage(String recievedMessage) {
        data.append(recievedMessage);
        extractMessage();
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isValidMessage() {
        return validMessage;
    }
    
    public void reset() {
        message = "";
        validMessage = false;
        data = new StringBuilder();
    }
    
    public static ByteBuffer createMessage(String msg) {
        String message = msg.length() + Command.DELIMITER + msg;
        return ByteBuffer.wrap(message.getBytes());
    }
    
    private void extractMessage() {
        String chars = data.toString();
        String[] parsedMessage = chars.split(Command.DELIMITER);
        if (parsedMessage.length < 2) {
            validMessage = false;
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

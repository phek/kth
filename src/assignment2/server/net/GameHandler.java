package assignment2.server.net;

import assignment2.server.model.HangmanGame;
import assignment2.shared.Command;
import assignment2.shared.MessageException;
import assignment2.shared.MessageHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

/**
 * Object that handles the communication with the client and the game.
 */
class GameHandler implements Runnable {

    private HangmanGame game = new HangmanGame();
    private final ByteBuffer messageFromClient = ByteBuffer.allocateDirect(8192);
    private MessageHandler messageHandler = new MessageHandler();
    private SocketChannel clientChannel;
    private Server server;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

    /**
     * Creates a new Game handler.
     *
     * @param server The server object.
     * @param clientChannel The connection to the client.
     */
    GameHandler(Server server, SocketChannel clientChannel) {
        this.server = server;
        this.clientChannel = clientChannel;
    }

    /**
     * Sends a welcome text to the client.
     */
    void sendWelcomeText() {
        queueMessage(
                "Welcome to the Hangman game server. The following commands are available:\n"
                + Command.PLAY + " - Starts a new hangman game\n"
                + Command.GET_SCORE + " - Gets your current score in the hangman game\n"
                + "All other input counts as guesses in the hangman game.\n"
                + "If a single characters is entered you're asking if the word contains this letter.\n"
                + "If a word is entered you're asking if the word is this word."
        );
    }

    /**
     * Handles new messages in a new thread.
     */
    @Override
    public void run() {
        try {
            String message = messageHandler.getMessage();
            messageHandler.reset();
            switch (message) {
                case Command.DISCONNECT:
                    disconnect();
                    break;
                case Command.PLAY:
                    queueMessage(game.newRound());
                    break;
                case Command.GET_SCORE:
                    queueMessage("Current score: " + game.getScore());
                    break;
                default:
                    if (game.isPlaying()) {
                        queueMessage(game.performGuess(message));
                    } else {
                        queueMessage("No active game found. Type " + Command.PLAY + " to play.");
                    }
                    break;
            }
        } catch (IOException failedToDisconnect) {
        }
    }

    /**
     * Reads messages from the connected client and handles the input.
     *
     * @throws IOException If failing to read the message.
     */
    void recieveMessage() throws IOException {
        messageFromClient.clear();

        int numOfReadBytes = clientChannel.read(messageFromClient);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }

        String recievedMessage = getMessageFromBuffer();
        messageHandler.appendReceivedMessage(recievedMessage);

        if (messageHandler.isValidMessage()) {
            ForkJoinPool.commonPool().execute(this);
        }
    }

    /**
     * Tries to send all messages in the queue.
     *
     * @throws IOException If a message couldn't be sent.
     */
    void sendMessages() throws IOException {
        ByteBuffer message;
        while ((message = messagesToSend.peek()) != null) {
            clientChannel.write(message);
            if (message.hasRemaining()) {
                throw new MessageException("Could not send message.");
            }
            messagesToSend.remove();
        }
    }

    /**
     * Closes the clients connection.
     *
     * @throws IOException If failing to close the connection.
     */
    void disconnect() throws IOException {
        clientChannel.close();
    }

    private void queueMessage(String message) {
        ByteBuffer bufMessage = MessageHandler.createMessage(message);
        synchronized (messagesToSend) {
            messagesToSend.add(bufMessage);
        }
        server.informUpdate(clientChannel);
    }

    private String getMessageFromBuffer() {
        messageFromClient.flip();
        byte[] bytes = new byte[messageFromClient.remaining()];
        messageFromClient.get(bytes);
        return new String(bytes);
    }
}

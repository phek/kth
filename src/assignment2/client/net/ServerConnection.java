package assignment2.client.net;

import assignment2.client.view.OutputHandler;
import assignment2.shared.Command;
import assignment2.shared.MessageException;
import assignment2.shared.MessageHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Object that handles server connection.
 */
public class ServerConnection implements Runnable {

    private final ByteBuffer messageFromServer = ByteBuffer.allocateDirect(8192);
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();
    private MessageHandler messageHandler = new MessageHandler();
    private OutputHandler toClient;

    private InetSocketAddress serverAddress;
    private SocketChannel serverChannel;
    private Selector selector;
    private boolean connected = false;
    private boolean newMessages = false;

    /**
     * Creates a new server connection object.
     *
     * @param output The Handler that controls output on the client.
     */
    public ServerConnection(OutputHandler output) {
        toClient = output;
    }

    /**
     * The communicating thread, all communication is non-blocking.
     */
    @Override
    public void run() {
        try {
            init();
            while (connected || !messagesToSend.isEmpty()) {
                if (newMessages) {
                    serverChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    newMessages = false;
                }
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection();
                        key.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        recieveFromServer();
                    } else if (key.isWritable()) {
                        sendMessages();
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (Exception e) {
            toClient.sendError("Lost connection.");
            toClient.sendError(e.getMessage());
        }
        try {
            disconnectFromChannel();
        } catch (IOException ex) {
            toClient.sendError("Couldn't disconnect. Continueing without disconnecting.");
        }
    }

    /**
     * Starts a new connection between the host and the server.
     *
     * @param host The server host.
     * @param port The server port.
     */
    public void connect(String host, int port) {
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    /**
     * Alerts the server that the client is disconnecting.
     */
    public void disconnect() {
        if (connected) {
            connected = false;
            queueMessage(Command.DISCONNECT);
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(String message) {
        if (connected) {
            queueMessage(message);
        } else {
            toClient.sendError("No connection found. Type !connect to connect to a game-server.");
        }
    }

    private void queueMessage(String message) {
        ByteBuffer bufMessage = MessageHandler.createMessage(message);
        messagesToSend.add(bufMessage);
        newMessages = true;
        selector.wakeup();
    }

    private void disconnectFromChannel() throws IOException {
        serverChannel.close();
        serverChannel.keyFor(selector).cancel();
        toClient.sendMessage("Disconnected.");
    }

    private void init() throws IOException {
        selector = Selector.open();
        setupServerChannel();
    }

    private void setupServerChannel() throws IOException {
        serverChannel = SocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.connect(serverAddress);
        serverChannel.register(selector, SelectionKey.OP_CONNECT);
        connected = true;
    }

    private void completeConnection() throws IOException {
        serverChannel.finishConnect();
        toClient.sendMessage("Connected to " + serverAddress.getHostString() + ":" + serverAddress.getPort());
    }

    private void recieveFromServer() throws IOException {
        messageFromServer.clear();
        int numOfReadBytes = serverChannel.read(messageFromServer);
        if (numOfReadBytes == -1) {
            throw new IOException("Lost connection.");
        }
        String recievedMessage = getMessageFromBuffer();
        messageHandler.appendRecievedMessage(recievedMessage);
        if (messageHandler.isValidMessage()) {
            String message = messageHandler.getMessage();
            messageHandler.reset();
            toClient.sendMessage(message);
        }
    }

    private void sendMessages() throws IOException {
        ByteBuffer message;
        while ((message = messagesToSend.peek()) != null) {
            serverChannel.write(message);
            if (message.hasRemaining()) {
                throw new MessageException("Couldn't send message.");
            }
            messagesToSend.remove();
        }
    }

    private String getMessageFromBuffer() {
        messageFromServer.flip();
        byte[] bytes = new byte[messageFromServer.remaining()];
        messageFromServer.get(bytes);
        return new String(bytes);
    }
}

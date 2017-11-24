package assignment2.server.net;

import assignment2.shared.MessageException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Object that handles the server.
 */
public class Server {

    private static final int PORT = 1337;
    private static final int LINGER_TIME = 5000;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;

    /**
     * Starts the server.
     */
    public void start() {
        try {
            init();
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        newClient(key);
                    } else if (key.isReadable()) {
                        recieveFromClient(key);
                    } else if (key.isWritable()) {
                        sendToClient(key);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Informs the server that new messages have been queued.
     *
     * @param clientChannel The connection to the client.
     */
    public void informUpdate(SocketChannel clientChannel) {
        clientChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    private void recieveFromClient(SelectionKey key) throws IOException {
        GameHandler gameHandler = (GameHandler) key.attachment();
        try {
            gameHandler.recieveMessage();
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void sendToClient(SelectionKey key) throws IOException {
        GameHandler gameHandler = (GameHandler) key.attachment();
        try {
            gameHandler.sendMessages();
            key.interestOps(SelectionKey.OP_READ);
        } catch (MessageException couldNotSendAllMessages) {
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void newClient(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        GameHandler gameHandler = new GameHandler(this, clientChannel);
        clientChannel.register(selector, SelectionKey.OP_WRITE, gameHandler);
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
        gameHandler.sendWelcomeText();

        System.out.println("New client connected");
    }

    private void removeClient(SelectionKey key) throws IOException {
        GameHandler gameHandler = (GameHandler) key.attachment();
        gameHandler.disconnect();
        key.cancel();
        System.out.println("A client disconnected");
    }

    private void init() throws IOException {
        selector = Selector.open();
        setupListeningChannel();
        System.out.println("Server started");
    }

    private void setupListeningChannel() throws IOException {
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(PORT));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
}

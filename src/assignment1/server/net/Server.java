package assignment1.server.net;

import assignment1.shared.OutputHandler;
import assignment1.shared.ThreadSafeOutput;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Object that handles new connections to the server.
 */
public class Server {

    private static final int PORT = 1337;
    private static final int LINGER_TIME = 5000;
    private ThreadSafeOutput output = new ThreadSafeOutput();

    /**
     * Starts listening for new connections.
     */
    public void start() {
        try {
            System.out.println("Server started");
            ServerSocket listeningSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                clientSocket.setSoLinger(true, LINGER_TIME);
                new Thread(new GameHandler(clientSocket, new ConsoleOutput())).start();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private class ConsoleOutput implements OutputHandler {

        @Override
        public void sendMessage(String msg) {
            output.println((String) msg);
        }
    }
}

package assignment1.client.net;

import assignment1.shared.Commands;
import assignment1.shared.OutputHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;

/**
 * Object that handles server sockets.
 */
public class ServerSocket {

    private final boolean AUTO_FLUSH = true;
    private final int TIMEOUT = 1000 * 60 * 10; //10 minutes

    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private OutputHandler toClient;
    private boolean connected = false;

    /**
     * Creates a new server socket.
     *
     * @param output The Handler that controls output on the client.
     */
    public ServerSocket(OutputHandler output) {
        toClient = output;
    }

    /**
     * Creates a new socket between the host and the server.
     *
     * @param host The server host.
     * @param port The server port.
     */
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(TIMEOUT);
            toServer = new PrintWriter(socket.getOutputStream(), AUTO_FLUSH);
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toClient.sendMessage("Connected successfully to: " + host);
            connected = true;
            new Thread(new Listener()).start();
        } catch (IOException ex) {
            toClient.sendMessage("Failed to connect with error: '" + ex.getMessage() + "'");
        }
    }

    /**
     * Destroys the socket and disconnects from the server.
     */
    public void disconnect() {
        try {
            if (socket != null) {
                sendMessage(Commands.DISCONNECT);
                socket.close();
                socket = null;
                connected = false;
                toClient.sendMessage("Disconnected");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(String message) {
        if (toServer != null) {
            toServer.println(message);
        } else {
            toClient.sendMessage("No connection found");
        }
    }

    private class Listener implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    toClient.sendMessage(fromServer.readLine());
                }
            } catch (IOException ex) {
                if (connected) {
                    disconnect();
                }
            }
        }
    }
}

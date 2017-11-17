package assignment1.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;

/**
 * Object that handles client sockets.
 */
public class ClientSocket {

    private final boolean AUTO_FLUSH = true;

    private Socket socket;
    private PrintWriter toClient;
    private BufferedReader fromClient;

    /**
     * Creates a new client socket.
     * @param socket The socket of the client.
     */
    ClientSocket(Socket socket) {
        try {
            this.socket = socket;
            this.toClient = new PrintWriter(socket.getOutputStream(), AUTO_FLUSH);
            this.fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Listens for a new message from the client.
     * @return The latest message from the client.
     * @throws IOException 
     */
    String recieveMessage() throws IOException {
        return fromClient.readLine();
    }

    /**
     * Sends a new message to the client.
     * @param message The message to be sent.
     */
    void sendMessage(String message) {
        toClient.println(message);
    }

    /**
     * Closes the socket.
     */
    void close() {
        try {
            socket.close();
            socket = null;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}

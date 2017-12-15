package assignment1.client.controller;

import assignment1.client.net.ServerSocket;
import assignment1.shared.OutputHandler;
import java.util.concurrent.CompletableFuture;

/**
 * The controller for a Client.
 */
public class ClientController {

    private final ServerSocket connection;

    /**
     * Creates a new controller.
     * @param output The handler that is responsible for output on the client.
     */
    public ClientController(OutputHandler output) {
        this.connection = new ServerSocket(output);
    }

    /**
     * Connects to a server.
     * @param host The server host.
     * @param port The server port.
     */
    public void connect(String host, int port) {
        CompletableFuture.runAsync(() -> connection.connect(host, port));
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        CompletableFuture.runAsync(() -> connection.disconnect());
    }

    /**
     * Sends a message to the server.
     * @param message The message to be sent.
     */
    public void sendMessage(String message) {
        CompletableFuture.runAsync(() -> connection.sendMessage(message));
    }
}

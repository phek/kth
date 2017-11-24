package assignment2.client.view;


import assignment2.shared.Command;
import assignment2.client.net.ServerConnection;
import java.util.Scanner;

/**
 * Object that handles the client.
 */
public class Client implements Runnable {

    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    private static final String PROMPT = "> ";

    private ThreadSafeOutput output = new ThreadSafeOutput();
    private final Scanner console = new Scanner(System.in);
    private ServerConnection connection = new ServerConnection(new ConsoleOutput());

    private boolean running = false;

    /**
     * Starts a new client.
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        output.println("Welcome, following commands are available:\n"
                + Command.QUIT + " - Quits the program\n"
                + Command.CONNECT + " - Connects to the game-server");
        while (running) {
            try {
                String input = getUserInput();
                switch (input) {
                    case Command.CONNECT:
                        connection.connect(HOST, PORT);
                        break;
                    case Command.QUIT:
                        running = false;
                        connection.disconnect();
                        break;
                    case "":
                        break;
                    default:
                        connection.sendMessage(input);
                        break;
                }
            } catch (Exception ex) {
                output.println("Operation failed.");
            }
        }
    }

    private String getUserInput() {
        output.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput implements OutputHandler {

        @Override
        public void sendMessage(String msg) {
            output.println((String) msg);
            output.print(PROMPT);
        }

        @Override
        public void sendError(String error) {
            output.printError(error);
        }
    }
}

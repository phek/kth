package assignment1.client.view;

import assignment1.shared.ThreadSafeOutput;
import assignment1.client.controller.ClientController;
import assignment1.shared.Commands;
import assignment1.shared.OutputHandler;
import java.util.Scanner;

/**
 * Object that handles the client.
 */
public class Client implements Runnable {

    private static final String HOST = "213.67.247.197";
    private static final int PORT = 1337;

    private static final String PROMPT = "> ";

    private ClientController controller;
    private ThreadSafeOutput output = new ThreadSafeOutput();
    private final Scanner console = new Scanner(System.in);

    private boolean running = false;

    /**
     * Starts a new client.
     */
    public void start() {
        if (running) {
            return;
        }
        controller = new ClientController(new ConsoleOutput());
        running = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        output.println("Welcome, following commands are available:\n"
                + Commands.QUIT + " - Quits the program\n"
                + Commands.CONNECT + " - Connects to the game-server");
        while (running) {
            try {
                String input = getUserInput();
                switch (input) {
                    case Commands.CONNECT:
                        controller.connect(HOST, PORT);
                        break;
                    case Commands.QUIT:
                        running = false;
                        controller.disconnect();
                        break;
                    case "":
                        break;
                    default:
                        controller.sendMessage(input);
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
    }
}

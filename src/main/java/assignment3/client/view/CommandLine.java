package assignment3.client.view;

import assignment3.shared.Input;
import assignment3.shared.Client;
import assignment3.shared.FileServer;
import assignment3.shared.LoginData;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class CommandLine implements Runnable {

    private final String DEFAULT_SERVER = "localhost";

    private String[] availableCommands = {
        Commands.LIST_COMMANDS + " - Lists all available client commands.",
        Commands.LOGIN + " - Logins to the file-server. Usage: " + Commands.LOGIN + " <username> <password>",
        Commands.LOGOUT + " - Logout from the file-server.",
        Commands.QUIT + " - Quit the program."
    };

    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeOutput output = new ThreadSafeOutput();
    private final Client client;
    private FileServer server;
    private long userID;
    private boolean running = false;
    private boolean connected = false;

    public CommandLine() throws RemoteException {
        client = new ConsoleOutput();
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input
     * when this method returns. Calling <code>start</code> on an interpreter
     * that is already started has no effect.
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        printCommands();
        while (running) {
            try {
                Input input = new Input(readInput());
                switch (input.getCommand()) {
                    case Commands.QUIT:
                        running = false;
                        logout();
                        break;
                    case Commands.LIST_COMMANDS:
                        printCommands();
                        if (connected) {
                            server.handleCommand(userID, input);
                        }
                        break;
                    case Commands.LOGIN:
                        if (!connected) {
                            lookupServer(DEFAULT_SERVER);
                            userID = server.login(client, new LoginData(input.getParam(0), input.getParam(1)));
                        } else {
                            output.printError("You're already logged in. Type: " + Commands.LOGOUT + " to logout.");
                        }
                        connected = true;
                        break;
                    case Commands.LOGOUT:
                        logout();
                        break;
                    default:
                        if (connected) {
                            server.handleCommand(userID, input);
                        } else {
                            output.println("You're not logged in.");
                        }
                }
            } catch (Exception ex) {
                output.println(ex.getMessage());
            }
        }
    }

    private void lookupServer(String host) throws NotBoundException, MalformedURLException, RemoteException {
        server = (FileServer) Naming.lookup("//" + host + "/" + FileServer.SERVER_REGISTRY_NAME);
    }

    private void logout() throws RemoteException {
        connected = false;
        if (server != null) {
            server.disconnect(userID);
            server = null;
        } else {
            output.println("You're not logged in.");
        }
    }

    private String readInput() {
        return console.nextLine();
    }

    private void printCommands() {
        output.println("The following client commands are available:");
        for (String command : availableCommands) {
            output.println(command);
        }
    }

    private class ConsoleOutput extends UnicastRemoteObject implements Client {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void receiveMessage(String msg) {
            output.println("<Server> " + msg);
        }
    }
}

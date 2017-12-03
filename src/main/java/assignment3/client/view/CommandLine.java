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

    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeOutput output = new ThreadSafeOutput();
    private final Client client;
    private FileServer server;
    private long userID;
    private boolean running = false;

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
        output.println("The following commands are available:");
        output.println(Commands.LOGIN + " - Logins to the file-server. Usage: login username password");
        output.println(Commands.QUIT + " - Quit the program.");
        while (running) {
            try {
                Input input = new Input(readInput());
                switch (input.getCommand()) {
                    case Commands.QUIT:
                        running = false;
                        logout();
                        break;
                    case Commands.LOGIN:
                        lookupServer(DEFAULT_SERVER);
                        userID = server.login(client, new LoginData(input.getParam(0), input.getParam(1)));
                        break;
                    case Commands.LOGOUT:
                        logout();
                        break;
                    default:
                        if (server != null) {
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
        if (server != null) {
            server.disconnect(userID);
            server = null;
            UnicastRemoteObject.unexportObject(client, false);
        } else {
            output.println("You're not logged in.");
        }
    }

    private String readInput() {
        return console.nextLine();
    }

    private class ConsoleOutput extends UnicastRemoteObject implements Client {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void receiveMessage(String msg) {
            output.println((String) msg);
        }
    }
}

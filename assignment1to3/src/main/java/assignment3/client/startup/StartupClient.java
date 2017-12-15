package assignment3.client.startup;

import assignment3.client.view.CommandLine;
import java.rmi.RemoteException;

/**
 * Starts the client.
 */
public class StartupClient {

    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new CommandLine().start();
        } catch (RemoteException ex) {
            System.out.println("Could not start client.");
        }
    }
}

package assignment3.server.startup;

import assignment3.server.controller.Controller;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Starts the server and binds it in the RMI registry.
 */
public class StartupServer {

    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new StartupServer().startRegistry();
            Naming.rebind(Controller.SERVER_REGISTRY_NAME, new Controller());
            System.out.println("Server is running.");
        } catch (MalformedURLException | RemoteException ex) {
            System.out.println("Could not start server.");
        }
    }

    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}

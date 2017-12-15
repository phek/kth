package assignment3.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServer extends Remote {

    /**
     * The default URI of the chat server in the RMI registry.
     */
    public static final String SERVER_REGISTRY_NAME = "FILE_SERVER";

    /**
     * Logs the user in on the server.
     *
     * @param client The remote endpoint of the user. This is the remote object
     * that will be used to send messages to the user.
     * @param loginData The credentials of the user.
     * @return The id of the user. A user must use this id for identification in
     * all communication with the server.
     */
    long login(Client client, LoginData loginData) throws RemoteException;

    /**
     * Handles the input from the client.
     *
     * @param userID The ID of the user.
     * @param input The input from the client.
     */
    void handleCommand(long userID, Input input) throws RemoteException;

    /**
     * The specified user is removed from the server, no more messages will be
     * sent to this node.
     *
     * @param userID The id of the leaving user.
     */
    void disconnect(long userID) throws RemoteException;
}

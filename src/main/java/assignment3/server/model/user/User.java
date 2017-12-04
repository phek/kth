package assignment3.server.model.user;

import assignment3.server.model.command.CommandException;
import assignment3.shared.Client;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Server user object.
 */
public class User {

    private UserData userData;
    private final Client client;
    private ArrayList<String> notifyFiles = new ArrayList<>();

    /**
     * Creates a new user.
     *
     * @param userData The data of the user.
     * @param client The remote node of the client.
     */
    public User(UserData userData, Client client) {
        this.userData = userData;
        this.client = client;
    }

    /**
     * Send the message to the client's remote node.
     *
     * @param message The message to send.
     */
    public void send(String message) {
        try {
            client.receiveMessage(message);
        } catch (RemoteException re) {
            throw new CommandException("Failed to deliver message to " + userData.getUsername() + ".");
        }
    }

    /**
     * Notify the user that the file has been changed.
     *
     * @param filename The file.
     * @param user The user that changed it.
     * @param action The action.
     */
    public void notifyChange(String filename, String user, String action) {
        for (String name : notifyFiles) {
            if (name.equals(filename)) {
                send(user + " " + action + " your file " + filename);
            }
        }
    }

    /**
     * Registers notifications on the file.
     *
     * @param filename The file.
     */
    public void addNotifier(String filename) {
        if (!notifyFiles.contains(filename)) {
            notifyFiles.add(filename);
        }
    }

    /**
     * Changes the username of the user.
     *
     * @param username The new username of the user.
     */
    public void changeUsername(String username) {
        this.userData.setUsername(username);
    }

    /**
     * @return The UserData object.
     */
    public UserData getUserData() {
        return userData;
    }

}

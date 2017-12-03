package assignment3.server.model.user;

import assignment3.server.model.command.CommandException;
import assignment3.shared.Client;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class User {

    private UserData userData;
    private final Client client;
    private ArrayList<String> notifyFiles = new ArrayList<>();

    public User() {
        this(null, null);
    }

    public User(UserData userData, Client client) {
        this.userData = userData;
        this.client = client;
    }

    /**
     * Send the specified message to the participant's remote node.
     *
     * @param msg The message to send.
     */
    public void send(String msg) {
        try {
            client.receiveMessage(msg);
        } catch (RemoteException re) {
            throw new CommandException("Failed to deliver message to " + userData.getUsername() + ".");
        }
    }

    public void notifyChange(String filename, String user, String action) {
        for (String name : notifyFiles) {
            if (name.equals(filename)) {
                send(user + " " + action + " your file " + filename);
            }
        }
    }

    public void addNotifier(String filename) {
        if (!notifyFiles.contains(filename)) {
            notifyFiles.add(filename);
        }
    }

    /**
     * @param username The new username of the user.
     */
    public void changeUsername(String username) {
        this.userData.setUsername(username);
    }

    public UserData getUserData() {
        return userData;
    }

}

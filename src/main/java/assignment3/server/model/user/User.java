package assignment3.server.model.user;

import assignment3.server.model.command.CommandException;
import assignment3.shared.Client;
import java.rmi.RemoteException;

public class User {

    private static final String DEFAULT_USERNAME = "anonymous";
    private final long id;
    private final Client client;
    private String username;

    /**
     * Creates a new instance with the specified username and remote node.
     *
     * @param id The unique identifier of this participant.
     * @param username The username of the newly created instance.
     * @param client The remote endpoint of the newly created instance.
     */
    public User(long id, String username, Client client) {
        this.id = id;
        this.username = username;
        this.client = client;
    }

    /**
     * Creates a new instance with the specified remote node and the default
     * username.
     *
     * @param id The unique identifier of this participant.
     * @param client The remote endpoint of the newly created instance.
     */
    public User(long id, Client client) {
        this(id, DEFAULT_USERNAME, client);
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
            throw new CommandException("Failed to deliver message to " + username + ".");
        }
    }

    /**
     * Checks if the specified remote node is the remote endpoint of this
     * participant.
     *
     * @param remoteNode The searched remote node.
     * @return <code>true</code> if the specified remote node is the remote
     * endpoint of this participant, <code>false</code> if it is not.
     */
    public boolean hasRemoteNode(Client remoteNode) {
        return remoteNode.equals(this.client);
    }

    /**
     * @param username The new username of the user.
     */
    public void changeUsername(String username) {
        this.username = username;
    }
    
    /** 
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

}

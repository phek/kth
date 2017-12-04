package assignment3.server.model.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of all online users on the server. This object is also
 * responsible for sending messages to the users.
 */
public class UserHandler {

    private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    /**
     * Adds the user as an online user.
     *
     * @param user The user.
     */
    public void addUser(User user) {
        users.put(user.getUserData().getUserID(), user);
    }

    /**
     * Adds the specified file to the notify list of the user.
     *
     * @param userID The users ID.
     * @param filename The filename of the file to get notified about.
     */
    public void addNotifier(long userID, String filename) {
        findUser(userID).addNotifier(filename);
    }

    /**
     * Notifies the creator that a change to the specified file has been done.
     *
     * @param creator The id of the user that created the file.
     * @param editor The id of the user that modified the file.
     * @param filename The file that was modified.
     * @param action The action that was performed.
     */
    public void notifyUser(long creator, long editor, String filename, String action) {
        User creatorUser = findUser(creator);
        if (creatorUser != null) {
            creatorUser.notifyChange(filename, getUsername(editor), action);
        }
    }

    /**
     * Sends a message to the specified user.
     *
     * @param userID The users ID.
     * @param message The message to be sent.
     */
    public void sendMessage(long userID, String message) {
        User user = users.get(userID);
        user.send(message);
    }

    /**
     * Removes the user from the online users.
     *
     * @param userID The users ID.
     */
    public void removeUser(long userID) {
        users.remove(userID);
    }

    /**
     * Changes the username of the user.
     *
     * @param userID The users ID.
     * @param username The new username.
     */
    public void changeUsername(long userID, String username) {
        User user = findUser(userID);
        user.changeUsername(username);
    }

    /**
     * Gets the username of the user.
     *
     * @param userID The users ID.
     * @return The username.
     */
    public String getUsername(long userID) {
        return findUser(userID).getUserData().getUsername();
    }

    /**
     * Broadcasts the message to all online users.
     *
     * @param message
     */
    public void broadcast(String message) {
        synchronized (users) {
            for (User user : users.values()) {
                user.send(message);
            }
        }
    }

    /**
     * Checks if the user is online.
     *
     * @param username The username of the user.
     * @return true if the user is online, false otherwise.
     */
    public boolean userIsLoggedIn(String username) {
        synchronized (users) {
            for (User user : users.values()) {
                if (user.getUserData().getUsername().equals(username)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Find the user find the specified user ID.
     *
     * @param userID The users ID.
     * @return The user if a user was found, null otherwise.
     */
    public User findUser(long userID) {
        return users.get(userID);
    }

}

package assignment3.server.model.user;

import assignment3.shared.Client;
import assignment3.shared.LoginData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserHandler {

    private final Random randomID = new Random();
    private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    public long loginUser(Client client, LoginData loginData) {
        String username = loginData.getUsername();
        if (userExists(username)) {
            throw new UserAlreadyExistsException("The username is already taken.");
        } else {
            long userID = randomID.nextLong();
            User newUser = new User(userID, loginData.getUsername(), client);
            users.put(userID, newUser);
            return userID;
        }
    }

    public void sendMessage(long id, String message) {
        User user = users.get(id);
        user.send(message);
    }

    /**
     * Removes the specified participant from the conversation. No more messages
     * will be sent to that participant.
     *
     * @param userID The id of the participant that shall be removed.
     */
    public void removeUser(long userID) {
        users.remove(userID);
    }
    
    public void changeUsername(long userID, String username) {
        if (userExists(username)) {
            sendMessage(userID, "Username already exists.");
        } else {
            User user = findUser(userID);
            user.changeUsername(username);
        }
    }
    
    public String getUsername(long userID) {
        return findUser(userID).getUsername();
    }

    public void broadcast(long userID, String message) {
        synchronized (users) {
            for (User user : users.values()) {
                user.send(message);
            }
        }
    }

    private boolean userExists(String username) {
        synchronized (users) {
            for (User user : users.values()) {
                if (user.getUsername().equals(username)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public User findUser(long id) {
        return users.get(id);
    }

}

package assignment3.server.model.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserHandler {

    private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    public void addUser(User user) {
        users.put(user.getUserData().getUserID(), user);
    }
    
    public void addNotifier(long userID, String filename) {
        findUser(userID).addNotifier(filename);
    }
    
    public void notifyUser(long creator, long editor, String filename, String action) {
        User creatorUser = findUser(creator);
        if (creatorUser != null) {
            creatorUser.notifyChange(filename, getUsername(editor), action);
        }
    }

    public void sendMessage(long userID, String message) {
        User user = users.get(userID);
        user.send(message);
    }

    public void removeUser(long userID) {
        users.remove(userID);
    }

    public void changeUsername(long userID, String username) {
        User user = findUser(userID);
        user.changeUsername(username);
    }

    public String getUsername(long userID) {
        return findUser(userID).getUserData().getUsername();
    }

    public void broadcast(long userID, String message) {
        synchronized (users) {
            for (User user : users.values()) {
                user.send(message);
            }
        }
    }

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

    public User findUser(long id) {
        return users.get(id);
    }

}

package assignment3.server.controller;

import assignment3.server.model.command.Commands;
import assignment3.server.model.file.FileHandler;
import assignment3.server.model.user.UserHandler;
import assignment3.server.model.user.UserNotFoundException;
import assignment3.shared.Client;
import assignment3.shared.FileServer;
import assignment3.shared.Input;
import assignment3.shared.LoginData;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Controller extends UnicastRemoteObject implements FileServer {

    private final UserHandler userHandler = new UserHandler();
    private final FileHandler fileHandler = new FileHandler();

    public Controller() throws RemoteException {
    }

    @Override
    public long login(Client client, LoginData loginData) {
        long userID = userHandler.loginUser(client, loginData);
        sendFilelistToUser(userID);
        return userID;
    }

    @Override
    public void handleCommand(long userID, Input input) {
        checkUser(userID);
        switch (input.getCommand()) {
            case Commands.ADD_FILE:
                String filename = input.getParam(0);
                String creator = userHandler.getUsername(userID);
                fileHandler.addFile(creator, filename);
                userHandler.broadcast(userID, filename + " added by " + creator);
                break;
            case Commands.CHANGE_NAME:
                String username = input.getParam(0);
                userHandler.changeUsername(userID, username);
                break;
            default:
                userHandler.sendMessage(userID, "Command not found.");
        }
    }

    @Override
    public void disconnect(long userID) {
        userHandler.removeUser(userID);
    }

    private void checkUser(long userID) {
        if (userHandler.findUser(userID) == null) {
            throw new UserNotFoundException("User not found.");
        }
    }

    private void sendFilelistToUser(long userID) {
        userHandler.sendMessage(userID, "- Existing files -");
        for (String filename : fileHandler.getFilelist()) {
            userHandler.sendMessage(userID, filename);
        }
    }
}

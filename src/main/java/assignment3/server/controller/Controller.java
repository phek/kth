package assignment3.server.controller;

import assignment3.server.integration.DatabaseHandler;
import assignment3.server.model.command.CommandException;
import assignment3.server.model.command.Commands;
import assignment3.server.model.file.File;
import assignment3.server.model.file.FileAlreadyExistsException;
import assignment3.server.model.user.User;
import assignment3.server.model.user.UserAlreadyExistsException;
import assignment3.server.model.user.UserData;
import assignment3.server.model.user.UserHandler;
import assignment3.shared.Client;
import assignment3.shared.FileServer;
import assignment3.shared.Input;
import assignment3.shared.LoginData;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Controller extends UnicastRemoteObject implements FileServer {

    private final DatabaseHandler db;
    private final UserHandler userManager;

    private String[] availableCommands = {
        Commands.LIST_FILES + " - Lists all available files.",
        Commands.LIST_COMMANDS + " - Lists all available server commands.",
        Commands.ADD_FILE + " - Adds a new file to the filesystem. Usage: " + Commands.ADD_FILE + " <filename> <permission [-|r|w|rw]> <content>",
        Commands.READ_FILE + " - Reads the file from the filesystem. Usage: " + Commands.READ_FILE + " <filename>",
        Commands.WRITE_FILE + " - Write to the file on the filesystem. Usage: " + Commands.WRITE_FILE + " <filename> <content>",
        Commands.REMOVE_FILE + " - Removes the file from the filesystem. Usage: " + Commands.REMOVE_FILE + " <filename>",
        Commands.NOTIFY_FILE + " - Get notifications of changes of the file. Usage: " + Commands.WRITE_FILE + " <filename>",
        Commands.CHANGE_NAME + " - Changes your current username. Usage: " + Commands.CHANGE_NAME + " <username>"
    };

    public Controller() throws RemoteException {
        super();
        db = new DatabaseHandler();
        userManager = new UserHandler();
    }

    @Override
    public long login(Client client, LoginData loginData) {
        if (userManager.userIsLoggedIn(loginData.getUsername())) {
            throw new UserAlreadyExistsException("User already logged in");
        }
        try {
            UserData user = db.getUser(loginData);
            if (user == null) { // Username and password wrong
                if (db.usernameExists(loginData.getUsername())) {
                    throw new UserAlreadyExistsException("Username already exists. Password didn't match");
                } else {
                    user = new UserData(loginData);
                    db.addUser(user);
                }
            }
            userManager.addUser(new User(user, client));
            sendCommandlist(user.getUserID());
            sendFilelistToUser(user.getUserID());
            return user.getUserID();
        } finally {
            db.commit();
        }
    }

    @Override
    public void handleCommand(long userID, Input input) {
        if (userManager.findUser(userID) == null) {
            throw new CommandException("Command not performed. You're not logged in");
        }
        switch (input.getCommand()) {
            case Commands.LIST_FILES:
                try {
                    sendFilelistToUser(userID);
                } finally {
                    db.commit();
                }
                break;
            case Commands.LIST_COMMANDS:
                sendCommandlist(userID);
                break;
            case Commands.ADD_FILE:
                String filename = input.getParam(0);
                try {
                    if (filename == null || db.fileExists(filename)) {
                        throw new FileAlreadyExistsException("Filename already exists");
                    }
                    String permission = input.getParam(1);
                    boolean read = false;
                    boolean write = false;
                    if (permission != null) {
                        switch (permission) {
                            case "r":
                                read = true;
                                break;
                            case "w":
                                write = true;
                                break;
                            case "rw":
                                read = true;
                                write = true;
                                break;
                        }
                    }
                    String content = input.getParamsAsString(2);
                    db.addFile(new File(filename, userID, read, write, content));
                    if (read || write) {
                        userManager.broadcast(userID, filename + " added by " + userManager.getUsername(userID));
                    } else {
                        userManager.sendMessage(userID, filename + " added");
                    }
                } finally {
                    db.commit();
                }
                break;
            case Commands.READ_FILE:
                filename = input.getParam(0);
                try {
                    File file = db.getFile(filename);
                    if (file != null) {
                        if (file.getCreator() == userID || file.isReadable()) {
                            userManager.sendMessage(userID, file.getContent());
                            if (file.getCreator() != userID) {
                                userManager.notifyUser(file.getCreator(), userID, filename, "read");
                            }
                        } else {
                            userManager.sendMessage(userID, "You do not have permission to do that");
                        }
                    } else {
                        userManager.sendMessage(userID, "File doesn't exist");
                    }
                } finally {
                    db.commit();
                }
                break;
            case Commands.NOTIFY_FILE:
                filename = input.getParam(0);
                try {
                    File file = db.getFile(filename);
                    if (file != null) {
                        if (file.getCreator() == userID) {
                            userManager.addNotifier(userID, filename);
                            userManager.sendMessage(userID, "You will now be notified of changes to " + filename);
                        } else {
                            userManager.sendMessage(userID, "You do not have permission to do that");
                        }
                    } else {
                        userManager.sendMessage(userID, "File doesn't exist");
                    }
                } finally {
                    db.commit();
                }
                break;
            case Commands.WRITE_FILE:
                filename = input.getParam(0);
                try {
                    File file = db.getFile(filename);
                    if (file != null) {
                        if (file.getCreator() == userID || file.isWriteable()) {
                            String content = input.getParamsAsString(1);
                            db.updateFileContent(filename, content);
                            userManager.sendMessage(userID, filename + " updated");
                            if (file.getCreator() != userID) {
                                userManager.notifyUser(file.getCreator(), userID, filename, "wrote to");
                            }
                        } else {
                            userManager.sendMessage(userID, "You do not have permission to do that");
                        }
                    } else {
                        userManager.sendMessage(userID, "File doesn't exist");
                    }
                } finally {
                    db.commit();
                }
                break;
            case Commands.REMOVE_FILE:
                filename = input.getParam(0);
                try {
                    File file = db.getFile(filename);
                    if (file != null) {
                        if (file.getCreator() == userID || file.isWriteable()) {
                            db.removeFile(filename);
                            userManager.sendMessage(userID, filename + " removed");
                            if (file.getCreator() != userID) {
                                userManager.notifyUser(file.getCreator(), userID, filename, "deleted");
                            }
                        } else {
                            userManager.sendMessage(userID, "You do not have permission to do that");
                        }
                    } else {
                        userManager.sendMessage(userID, "File doesn't exist");
                    }
                } finally {
                    db.commit();
                }
                break;
            case Commands.CHANGE_NAME:
                String newName = input.getParam(0);
                try {
                    if (!db.usernameExists(newName)) {
                        db.changeUsername(userManager.getUsername(userID), newName);
                        userManager.changeUsername(userID, newName);
                        userManager.sendMessage(userID, "Username updated to " + newName);
                    } else {
                        userManager.sendMessage(userID, "Username already exists");
                    }
                } finally {
                    db.commit();
                }
                break;
            default:
                userManager.sendMessage(userID, "Command not found");
        }
    }

    @Override
    public void disconnect(long userID) {
        userManager.removeUser(userID);
    }

    private void sendFilelistToUser(long userID) {
        List<File> files = db.getAllFiles();
        userManager.sendMessage(userID, "Listing existing files:");
        for (File file : files) {
            String read = "";
            String write = "";
            if (file.isReadable()) {
                read = "r";
            }
            if (file.isWriteable()) {
                write = "w";
            }
            String fileString = file.getFilename() + " <" + read + write + ">" + " (" + file.getSize() + " kb)";
            if (file.isPublic()) {
                userManager.sendMessage(userID, fileString);
            } else {
                if (userID == file.getCreator()) {
                    userManager.sendMessage(userID, fileString);
                }
            }
        }
    }

    private void sendCommandlist(long userID) {
        userManager.sendMessage(userID, "The following server commands are available:");
        for (String command : availableCommands) {
            userManager.sendMessage(userID, command);
        }
    }
}

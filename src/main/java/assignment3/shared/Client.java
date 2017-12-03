package assignment3.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    
    /**
     * The specified message is received by the client.
     *
     * @param msg The message that shall be received.
     */
    void receiveMessage(String msg) throws RemoteException;
}
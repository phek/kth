package assignment5.net;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import assignment5.view.MainActivity;

public class ConnectionService extends Service {

    private final IBinder binder = new MyBinder();

    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private boolean connected = false;
    private String host;
    private int port;
    private Intent broadcastIntent = new Intent();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (connected && host != null) {
            connect(host, port);
        }
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        disconnect();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
    }

    public class MyBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    /**
     * Creates a new socket between the host and the server.
     *
     * @param server_host The server host.
     * @param server_port The server port.
     */
    public void connect(String server_host, int server_port) {
        host = server_host;
        port = server_port;
        if (!connected) {
            new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Destroys the socket and disconnects from the server.
     */
    public void disconnect() {
        new DisonnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendMessage(String message) {
        new MessageSender().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
    }

    private class MessageSender extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... message) {
            if (toServer != null) {
                toServer.println(message[0]);
            } else {
                alertUser("No connection found");
            }
            return null;
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                boolean AUTO_FLUSH = true;
                int TIMEOUT = 1000 * 60 * 10;
                socket = new Socket(host, port);
                socket.setSoTimeout(TIMEOUT);
                toServer = new PrintWriter(socket.getOutputStream(), AUTO_FLUSH);
                fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                alertUser("Connected successfully to: " + host);
                connected = true;
                alertConnected();
                while (true) {
                    alertUser(fromServer.readLine());
                }
            } catch (Exception ex) {
                if (connected) {
                    disconnect();
                }
                alertUser(ex.getMessage());
            }
            return null;
        }
    }

    private class DisonnectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (socket != null) {
                    toServer.println(Commands.DISCONNECT);
                    socket.close();
                    socket = null;
                    connected = false;
                    alertUser("Disconnected");
                }
                alertDisconnected();
            } catch (IOException ex) {
                alertUser("Failed to disconnect from server.");
            }
            return null;
        }
    }

    private void alertUser(String message) {
        broadcastIntent.setAction(MainActivity.BROADCAST_MSG);
        broadcastIntent.putExtra("Message", message);
        sendBroadcast(broadcastIntent);
    }

    private void alertConnected() {
        broadcastIntent.setAction(MainActivity.CONNECTED_MSG);
        sendBroadcast(broadcastIntent);
    }

    private void alertDisconnected() {
        broadcastIntent.setAction(MainActivity.DISCONNECTED_MSG);
        sendBroadcast(broadcastIntent);
    }
}

package assignment5.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import assignment5.net.ConnectionService;
import assignment5.R;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    public static final String BROADCAST_MSG = "!broadcast";
    public static final String CONNECTED_MSG = "!connected";
    public static final String DISCONNECTED_MSG = "!disconnected";

    private static final int PORT = 1337;
    private ConnectionService connectionService;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        Intent intent= new Intent(this, ConnectionService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        ConnectionService.MyBinder b = (ConnectionService.MyBinder) binder;
        connectionService = b.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connectionService = null;
    }

    private void setup() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_MSG);
        intentFilter.addAction(CONNECTED_MSG);
        intentFilter.addAction(DISCONNECTED_MSG);
        setupConnectButton();
        setupSendButton();
    }

    private void setupSendButton() {
        final Button sendMessageButton = findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (connectionService.isConnected()) {
                    EditText messageText = findViewById(R.id.messageText);
                    String msg = messageText.getText().toString();
                    if (!msg.equals("")) {
                        connectionService.sendMessage(msg);
                        addToConsole(msg);
                        messageText.setText("");
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Not connected")
                            .setTitle(R.string.no_connection);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void setupConnectButton() {
        final Button connectButton = findViewById(R.id.connectButton);
        connectButton.setText("Connect");
        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v)
            {
                v.getContext();
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
                if (networkInfo != null && networkInfo.isConnected())
                {
                    EditText serverText = findViewById(R.id.serverText);
                    connectionService.connect(serverText.getText().toString(), PORT);
                } else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.no_internet)
                            .setTitle(R.string.no_connection);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void setupDisconnectButton() {
        final Button connectButton = findViewById(R.id.connectButton);
        connectButton.setText("Disconnect");
        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v)
            {
            connectionService.disconnect();
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_MSG)) {
                addToConsole(intent.getStringExtra("Message"));
            } else if (intent.getAction().equals(CONNECTED_MSG)) {
                setupDisconnectButton();
            } else if (intent.getAction().equals(DISCONNECTED_MSG)) {
                setupConnectButton();
            }
        }
    };

    private void addToConsole(String msg) {
        int maxLength = 15;
        final TextView console = findViewById(R.id.console);
        String defaultText = console.getText().toString();
        String[] textDivided = defaultText.split("[\n|\r]");
        StringBuilder limitedText = new StringBuilder();
        int length = textDivided.length;
        if (length > maxLength) {
            for (int i = length - maxLength + 1; i < length; i++) {
                limitedText.append(textDivided[i]).append("\n");
            }
        } else {
            limitedText = new StringBuilder(defaultText);
        }
        limitedText.append(msg).append("\n");
        console.setText(limitedText);
    }
}

package nl.verhoogenvansetten.gamrio.util.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.GameListActivity;
import nl.verhoogenvansetten.gamrio.ui.DeviceDialogFragment;

/**
 * -------------------------------------
 * Using the Network class in your game.
 * -------------------------------------
 * This network class handles the management of the WiFi P2P connection.
 * It also handles the sending and receiving of information which it will pass on to a GameCompat
 * object.
 * <p>
 * To use this class you first have to make an activity that inherits from the GameCompat class.
 * <p>
 * You need to get an object of this class by calling the
 * Network.getInstance()
 * function in de onCreate().
 * <p>
 * You need to register your game in de onResume function and pass the ID of your game and an object
 * of your games activity.
 * network.registerGame(int ID, GameCompat this)
 * <p>
 * You also need to unregister your game in the onPause class to not get updated when your activity
 * is paused.
 * network.unregisterGame(int ID)
 * <p>
 * The update(String data) function also has to be overridden. This function will be called every
 * time a message is received for your game. This function will be called with data = null if the
 * previous attempt at sending failed.
 * <p>
 * The peerDown() and peerUp() functions will be called every time the peer closes or opens the
 * current game.
 * <p>
 * To send messages the network.send(int ID, String message) has to be called with the game ID and a
 * string of the message. This function will return true if the message was sent and false if the ID
 * on the other machine doesn't match the one in the current one.
 * <p>
 * -------------------------------------------------
 * Getting other information from the Network class.
 * -------------------------------------------------
 * int getOtherGameID() returns the game the other payer is playing. 0 will be returned if no game
 * is running.
 * <p>
 * boolean isGroupOwner() will return true if the current device is the WiFi P2P group owner.
 * This can be useful if some asynchronous processing is required to differentiate between a host
 * and client.
 * <p>
 * void enableWifi() will enable the wifi on the system.
 * <p>
 * String getConnectedDeviceName will return the name of the connected peer.
 */

public class Network {

    private static WifiP2pManager mManager;
    private static WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private int ID;
    private GameCompat game;
    private String ip;
    private String iface;
    private int port = 12346;
    private boolean isOwner;
    private static Network instance;
    private Server server;
    private int otherGameID = 0;
    private String peerName = "";
    private boolean isConnected;

    public static final int BATTLESHIP = 1;
    public static final int BINGO = 2;
    public static final int CHECKERS = 3;
    public static final int DOTSANDBOXES = 4;
    public static final int FOURINAROW = 5;
    public static final int SUDOKU = 6;
    public static final int TICTACTOE = 7;

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }

    private Network() {
        Context main = GameListActivity.getContext();
        mManager = (WifiP2pManager) main.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(main, main.getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(this, mManager, mChannel);
    }

    public void discoverPeers(final FragmentManager manager) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                DeviceDialogFragment fragment = DeviceDialogFragment.newInstance();
                fragment.show(manager, "device_list_fragment_dialog");
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });

    }

    void setDeviceList(List l) {
        DeviceDialogFragment.filterAdapter(l);
    }

    public void registerGame(int id, GameCompat game) {
        ID = id;
        this.game = game;
        if (ID >= 10)
            startServer();
        if (ID > 10) {
            send(10, "0\n" + Integer.toString(id));
        } else {
            send(0, "0\n" + Integer.toString(id));
        }

    }

    public void unregisterGame(int id) {
        if (id == ID) {
            if (ID > 10) {
                send(10, "1\n" + "0");
            } else
                send(0, "1\n" + "0");
            if (ID >= 10)
                onDestroy();
            ID = 0;
            this.game = null;
        }
    }

    public String getConnectedDeviceName() {
        return peerName;
    }

    void setPeerName(String name) {
        peerName = name;
    }

    public static void connect(WifiP2pConfig config, final Activity activity) {
        try {
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(activity, "Successfully connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(activity, "Could not connect. Restart your wifi and try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void enableWifi() {
        WifiManager wifi = (WifiManager) GameListActivity.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifi.setWifiEnabled(true);
        }
    }

    void afterSend(boolean status) {
        if (!status) {
            try {
                game.update(null);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean send(int ID, String m) {

        if (ID != otherGameID && ID != 0 && ID != 10)
            return false;

        String address;
        String message = Integer.toString(ID) + "\n" + m;

        if (ip == null)
            ip = getIpFromArpCache(iface);

        if (ID >= 10)
            address = "127.0.0.1";
        else
            address = ip;

        Client client = new Client(this, ip, port, message);
        client.execute();
        return true;
    }

    public BroadcastReceiver getReceiver() {
        return mReceiver;
    }

    void setIp(String ip) {
        this.ip = ip;
    }

    void setIface(String iface) {
        this.iface = iface;
    }

    private String getIpFromArpCache(String iface) {
        if (iface == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4 && iface.equals(splitted[5])) {
                    // Basic sanity check
                    String ip = splitted[0];
                    if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                        return ip;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    void update(String data) {
        try {
            data = data.substring(0, data.length() - 1);
            String data2[] = data.split("\n", 2);
            int id = Integer.valueOf(data2[0]);

            data = data2[1];

            if (id == ID) {
                otherGameID = id;
                game.update(data);
            } else if (id == 0) {
                data2 = data.split("\n", 2);
                id = Integer.valueOf(data2[1]);
                if(data2[0].equals("0")) {
                    otherGameID = id;
                    send(0, "1\n" + ID);
                } else if (data2[0].equals("1"))
                    otherGameID = id;
                if (otherGameID == ID)
                    game.peerUp();
                else if (otherGameID == 0)
                    game.peerDown();
            }
        } catch (StringIndexOutOfBoundsException e) {
            Log.e("Network", data);
            e.printStackTrace();
            throw e;
        }
    }

    void startServer() {
        if (Server.serverSocket == null)
            server = new Server(this);
    }

    public void onDestroy() {
        try {
            server.onDestroy();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    int getPort() {
        return port;
    }

    void setOwner(boolean is) {
        isOwner = is;
    }

    public boolean isGroupOwner() {
        return isOwner;
    }

    public int getOtherGameID() {
        return otherGameID;
    }

    public GameCompat getGame() {
        try {
            return game;
        } catch (NullPointerException e) {
            throw e;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}

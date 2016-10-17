package nl.verhoogenvansetten.gamrio.util;

//import nl.verhoogenvansetten.gamrio.SignInDialogFragment;
import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.GameListActivity;
import nl.verhoogenvansetten.gamrio.model.Game;
import nl.verhoogenvansetten.gamrio.ui.DeviceDialogFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by bloodyfool on 12-10-16.
 */

public class Network {

    private static WifiP2pManager mManager;
    private static WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private int ID;
    private GameCompat game;
    //private Activity main;
    private String ip;
    private String iface;
    private int port = 12345;
    private boolean isConnected;
    IntentFilter mIntentFilter;
    private static Network instance;

    public static Network getInstance() {
        if (instance==null)
            instance = new Network();
        return instance;
    }

    public Network() {
        //TODO
        Context main = GameListActivity.getContext();
        mManager = (WifiP2pManager) main.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(main, main.getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(this, mManager, mChannel);
        //this.main = main;

        //get broadcast receiver
    }

    public void discoverPeers(final FragmentManager manager) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(main, "scanning", Toast.LENGTH_SHORT).show();
                DeviceDialogFragment fragment = DeviceDialogFragment.newInstance();
                fragment.show(manager, "device_list_fragment_dialog");
            }

            @Override
            public void onFailure(int reasonCode) {
                //TODO main.chipper("OnFailure");
            }
        });

    }

    void setDeviceList(List l) {
        DeviceDialogFragment.filterAdapter(l);
    }

    public void registerGame(int id, GameCompat game) {
        ID = id;
        this.game = game;
    }

    public void unregisterGame() {
        ID = 0;
        this.game = null;
        //TODO send shutdown message
    }

    public String getConnectedDeviceName() {
        //TODO return connected device name
        return "";
    }

    public static void connect(WifiP2pConfig config, final Activity activity) {
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
    }

    public Boolean sendData(int id, String data) {
        //TODO send data
        return true;
    }

    public void enableWifi() {
        WifiManager wifi = (WifiManager) GameListActivity.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifi.setWifiEnabled(true);
        }
    }

    void afterSend(boolean status) {
        if(!status) {
            // TODO game.setText("Send failed");
        }
    }

    public void send(int ID, String message) {
        if(ip == null)
            ip = getIpFromArpCache(iface);
        Client client = new Client(this, ip, port, message);
        client.execute();
    }

    public BroadcastReceiver getReceiver() {
        return mReceiver;
    }

    void setConnected(boolean state) {
        isConnected = state;
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
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void update(String data) {
        try {
            game.update(data);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public GameCompat getGame() {
        return game;
    }
}

package nl.verhoogenvansetten.gamrio.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.GameListActivity;
import nl.verhoogenvansetten.gamrio.ui.DeviceDialogFragment;

/**
 * Created by bloodyfool on 12-10-16.
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
    private boolean isConnected;
    private static Network instance;
    private Server server;

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }

    private Network() {
        //TODO
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
                //TODO main.chipper("OnFailure");
            }
        });

    }

    protected void setDeviceList(List l) {
        DeviceDialogFragment.filterAdapter(l);
    }

    public void registerGame(int id, GameCompat game) {
        ID = id;
        this.game = game;
        if(ID>=10)
            startServer();
    }

    public void unregisterGame() {
        if(ID>=10)
            onDestroy();
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
        new Client(this, ip, port, data).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //TODO check for id
        return true;
    }

    public void enableWifi() {
        WifiManager wifi = (WifiManager) GameListActivity.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifi.setWifiEnabled(true);
        }
    }

    protected void afterSend(boolean status) {
        if (!status) {
            game.update(null);
        }
    }

    public void send(int ID, String message) {

        String address;

        if (ip == null)
            ip = getIpFromArpCache(iface);

        if (ID >= 10)
            address = "127.0.0.1";
        else
            address = ip;

        Client client = new Client(this, address, port, message);
        client.execute();
    }

    public BroadcastReceiver getReceiver() {
        return mReceiver;
    }

    protected void setConnected(boolean state) {
        isConnected = state;
    }

    protected void setIp(String ip) {
        this.ip = ip;
    }

    protected void setIface(String iface) {
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

    protected void update(String data) {
        try {
            game.update(data);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    protected void startServer() {
        if (Server.serverSocket == null)
            server = new Server(this);
    }

    public void onDestroy() {
        try {
            server.onDestroy();
        } catch (NullPointerException e) {

        }
    }

    protected int getPort() {
        return port;
    }

    public GameCompat getGame() {
        return game;
    }
}

package nl.verhoogenvansetten.gamrio.util;

import nl.verhoogenvansetten.gamrio.model.Game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.verhoogenvansetten.gamrio.GameDetailActivity;

/**
 * Created by bloodyfool on 12-10-16.
 */

public class Network {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private int ID;
    private Game game;
    private GameDetailActivity main;
    private String ip;
    private String iface;
    private int port = 12345;
    private boolean isConnected;


    public Network(GameDetailActivity main) {
        //TODO
        mManager = (WifiP2pManager) main.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(main, main.getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(this, mManager, mChannel, main);
        this.main = main;

        //get broadcast receiver
    }

    public void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
             public void onSuccess() {
            //TODO    main.chipper("OnSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                //TODO main.chipper("OnFailure");
            }
        });

    }

    public void setDeviceList(List l) {
        // TODO main.setDeviceList(l);
    }

    public void registerGame(int id, Game game) {
        ID = id;
        this.game = game;
        //TODO send a startup message
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

    public void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
                // TODO main.chipper("Connect failed. Retry.");
            }
        });
    }

    public Boolean sendData(int id, String data) {
        //TODO send data
        return true;
    }

    public void enableWifi() {
        WifiManager wifi = (WifiManager) main.getSystemService(Context.WIFI_SERVICE);
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifi.setWifiEnabled(true);
        }
    }

    public void afterSend(boolean status) {
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

    public void setConnected(boolean state) {
        isConnected = state;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public String getIpFromArpCache(String iface) {
        if (iface == null)
            return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && iface.equals(splitted[5])) {
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
}

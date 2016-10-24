package nl.verhoogenvansetten.gamrio.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    Network network;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    //private Activity mActivity;
    private List peers = new ArrayList();
    Server server;

    public WiFiDirectBroadcastReceiver(Network network, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        super();
        this.network= network;
        this.mManager = manager;
        this.mChannel = channel;
        //this.mActivity = activity;
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            peers.clear();
            peers.addAll(peerList.getDeviceList());
            network.setDeviceList(peers);
        }

    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
            //Toast(MainActivity.this , "P2P peers changed", Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            NetworkInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (info.isConnected()) {
                //setup server
                network.setConnected(true);
                if(Server.serverSocket == null) {
                    server = new Server(network);
                    network.update("server started");
                } else {
                    network.update("server restart attempted");
                }
                //mActivity.infoip.setText(server.getIpAddress() + ":" + server.getPort());

                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        try {
                            String iface = group.getInterface();
                            String ipaddress = getIpFromArpCache(iface);
                            network.setIp(ipaddress);
                            network.setIface(iface);
                            // TODO mActivity.chipper(ipaddress);
                        } catch (NullPointerException e) {
                            // TODO mActivity.chipper("no current group");
                        }

                    }
                });

            } else {
                try {
                    server.onDestroy();
                } catch (NullPointerException e) {}
                network.setConnected(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
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
            // TODO mActivity.chipper("exception");
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Server getServer() {
        return server;
    }

}


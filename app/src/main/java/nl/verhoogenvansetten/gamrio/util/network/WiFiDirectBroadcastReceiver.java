package nl.verhoogenvansetten.gamrio.util.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    Network network;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private List peers = new ArrayList();

    public WiFiDirectBroadcastReceiver(Network network, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        super();
        this.network = network;
        this.mManager = manager;
        this.mChannel = channel;
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

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // get list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            NetworkInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (info.isConnected()) {
                //setup server
                network.startServer();
                network.setConnected(true);

                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        try {
                            String iface = group.getInterface();
                            String ipaddress = getIpFromArpCache(iface);
                            network.setIp(ipaddress);
                            network.setIface(iface);
                            network.setOwner(group.isGroupOwner());
//                            network.setPeerName(group.getClientList().iterator().next().deviceName);
                        } catch (NullPointerException | NoSuchElementException e) {
                            e.printStackTrace();
                        }

                    }
                });

            } else {
                try {
                    network.onDestroy();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                network.setPeerName("");
                network.setConnected(false);
            }
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
                    // Sanity check
                    String ip = splitted[0];
                    if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                        return ip;
                    } else {
                        return null;
                    }
                }
            }
        } catch (IOException e) {
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

}


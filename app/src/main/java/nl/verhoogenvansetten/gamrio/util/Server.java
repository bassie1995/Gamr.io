package nl.verhoogenvansetten.gamrio.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import nl.verhoogenvansetten.gamrio.GameDetailActivity;

/**
 * Created by bloodyfool on 11-10-16.
 */

public class Server {
    GameDetailActivity activity;
    ServerSocket serverSocket;
    String message = "";
    //String reply = "";
    static final int socketServerPORT = 12345;

    public Server(GameDetailActivity activity) {
        this.activity = activity;
        Thread sockerServerThread = new Thread(new SocketServerThread());
        sockerServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //activity.infoip.setText("Connection Lost");
    }

    private class SocketServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(getPort());

                while (true) {
                    Socket socket = serverSocket.accept();
                    //count++;
                    //message += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n";

                    /**activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.msg.setText(message);
                        }
                    });**/

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            //String msgReply = "Hello from server, you are #" + cnt;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(hostThreadSocket.getInputStream()));

                /**for (String data; (data = bufferedReader.readLine()) != null; ) {
                    reply += "\n" + data;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //activity.response.setText(reply);
                        reply = "";
                    }
                });**/

                bufferedReader.close();

                /**outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
//                printStream.close();

                message += "replayed: " + msgReply + "\n";
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.msg.setText(message);
                    }
                });**/
            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO activity.msg.setText(message);
                }
            });
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress() + networkInterface.getDisplayName();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

}

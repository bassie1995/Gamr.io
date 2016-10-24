package nl.verhoogenvansetten.gamrio.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.AsyncTask;

/**
 * Created by bloodyfool on 11-10-16.
 */

class Server {
    //private Activity activity;
    static ServerSocket serverSocket;
    private String message = "";
    //String reply = "";
    private static final int socketServerPORT = 12346;
    Network network;

    Server(Network network) {
        this.network = network;
        //this.activity = activity;
        //Thread sockerServerThread = new Thread(new SocketServerThread());
        //sockerServerThread.start();
        new SocketServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int getPort() {
        return socketServerPORT;
    }

    void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //activity.infoip.setText("Connection Lost");
    }

    //private class SocketServerThread extends Thread {
    private class SocketServerTask extends AsyncTask<Void, Void, Void> {
        int count = 0;

        @Override
        //public void run() {
        protected Void doInBackground(Void... params) {
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

                    //SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    //socketServerReplyThread.run();
                    //SocketServerReplyTask task = new SocketServerReplyTask(socket, count).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    class StartTask implements Runnable {
                        Socket s;

                        StartTask(Socket s) {
                            this.s = s;
                        }

                        @Override public void run() {
                            new SocketServerReplyTask(s, count).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }



                    network.getGame().runOnUiThread(new StartTask(socket));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //private class SocketServerReplyThread extends Thread {
    private class SocketServerReplyTask extends AsyncTask<Void, Void, Void> {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyTask(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        //public void run() {
        protected Void doInBackground(Void... params) {
            OutputStream outputStream;
            //String msgReply = "Hello from server, you are #" + cnt;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(hostThreadSocket.getInputStream()));

                for (String data; (data = bufferedReader.readLine()) != null; ) {
                    message += "\n" + data;
                }
                /**
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

            network.getGame().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    network.update(message);
                    //network.update("message");
                }
            });
            message = "";
            return null;
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

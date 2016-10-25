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

public class Server {
    static ServerSocket serverSocket;
    private String message = "";
    Network network;

    Server(Network network) {
        this.network = network;
        new SocketServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerTask extends AsyncTask<Void, Void, Void> {
        int count = 0;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                serverSocket = new ServerSocket(network.getPort());

                while (true) {
                    Socket socket = serverSocket.accept();

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
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(hostThreadSocket.getInputStream()));

                for (String data; (data = bufferedReader.readLine()) != null; ) {
                    message += data + "\n";
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
                    message = "";
                }
            });
            return null;
        }
    }
}

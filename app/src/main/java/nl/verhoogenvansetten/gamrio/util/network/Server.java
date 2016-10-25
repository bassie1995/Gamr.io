package nl.verhoogenvansetten.gamrio.util.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.AsyncTask;

/**
 * This class manages the server side of the communication. Access to this class should only be done
 * through the functions of the Network class.
 */

class Server {
    static ServerSocket serverSocket;
    private String message = "";
    private Network network;

    Server(Network network) {
        this.network = network;
        new SocketServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    void onDestroy() {
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
                        private Socket s;

                        private StartTask(Socket s) {
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
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(hostThreadSocket.getInputStream()));

                for (String data; (data = bufferedReader.readLine()) != null; ) {
                    message += data + "\n";
                }
                bufferedReader.close();

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

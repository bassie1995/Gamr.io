package nl.verhoogenvansetten.gamrio.util;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by bloodyfool on 11-10-16.
 */

public class Client extends AsyncTask<Void, Void, Void> {

    private String dstAddress;
    private int dstPort;
    private boolean response;
    private String message;
    Network network;

    Client(Network network, String addr, int port, String theMessage) {
        this.network = network;
        dstAddress = addr;
        dstPort = port;
        message = theMessage;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.print(message);
            printStream.close();
            response = true;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = false;
        } catch (IOException e) {
            e.printStackTrace();
            response = false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        network.afterSend(response);
        super.onPostExecute(result);
    }
}

package nl.verhoogenvansetten.gamrio.util.network;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * This class manages the client side of the communication. Access to this class should only be done
 * through the functions of the Network class.
 */

class Client extends AsyncTask<Void, Void, Void> {

    private String dstAddress;
    private int dstPort;
    private String message;

    Client(String addr, int port, String theMessage) {
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

        } catch (IOException e) {
            e.printStackTrace();
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
        super.onPostExecute(result);
    }
}

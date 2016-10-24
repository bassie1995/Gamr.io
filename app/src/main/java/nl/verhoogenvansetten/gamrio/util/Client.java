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
        network.update("sent");
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            /**ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();


            //notice: inputStream.read() will block if no data return

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }**/

            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.print(message);
            printStream.close();
            response = true;
            /**BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            for (String data; (data = bufferedReader.readLine()) != null; ) {
                response = data;
            }

            bufferedReader.close();**/

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

package nl.verhoogenvansetten.gamrio.games.fourinrow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.games.battleship.ui.BattleshipGameActivity;
import nl.verhoogenvansetten.gamrio.util.network.Network;

public class FourInARowActivity extends GameCompat {

    private TextView dataView;
    private Network network;
    private int ID = 1;
    private int[][] idGrid = new int[8][8];
    private String[][] valueGrid = new String[8][8];
    private boolean lock;
    private boolean running;

    private int count = 0;

    private String localPlayer = "X";
    private String otherPlayer = "O";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_in_a_row);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add bar options
                network.send(ID, "helloooo");
                //dataView.setText(String.valueOf(network.getOtherGameID()));
                //dataView.setText(network.getConnectedDeviceName());
                //clearBoard();
            }
        });

        dataView = (TextView) findViewById(R.id.dataview);

        network = Network.getInstance();

        setIdGrid();

        clearBoard();

        unlock();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fourinrow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_play_fourinrow:

                return true;
            case R.id.menu_restart_fourinrow:
                clearBoard();
                unlock();
                sendRestart();
                return true;
            case R.id.menu_settings_fourinrow:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v) {

        if (lock || !running) {
            if (lock)
                dataView.setText("locked");
            if (!running)
                dataView.setText("notRunning");
            return;
        }

        Button b = (Button) v;

        String x = String.valueOf(b.getTag().toString().charAt(1));
        String y = String.valueOf(b.getTag().toString().charAt(2));

        if(setCoords(x, y, "X"))
            if (localWin(x, y)) {
                dataView.setText("sending Win");
                sendWin(x, y);
            } else {
                dataView.setText("sending coords");
                sendCoord(x, y);
            }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Communication functions
     * ---------------------------------------------------------------------------------------------
     */

    public void update(String data) {
        String[] typeData = data.split("\n", 2);
        switch (typeData[0]) {
            case "1":
                recvCoord(typeData[1]);
                break;
            case "2":
                recvWin(typeData[1]);
                break;
            case "3":
                recvBoard(typeData[1]);
                break;
            case "4":
                recvStart(typeData[1]);
                break;
            case "5":
                recvRestart();
                break;
            case "6":
                recvUpdate(typeData[1]);
                break;
            default:
                this.dataView.setText("Weird message received");

        }
    }

    public void peerDown() {
        dataView.setText("peerDown");
        Snackbar.make(findViewById(R.id.content_four_in_arow), "Peer Down!!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        running = false;
    }

    public void peerUp() {
        dataView.setText("peerUp");
        running = true;
        sendBoard();
    }

    /**
     * Communication helper functions.
     */
    private void recvUpdate(String data) {
        otherPlayer = data;
        pushBoard();
    }

    private void sendUpdate() {
        /** 6 to indicate that this is a meta update message*/
        String message = "6\n";
        message += localPlayer;
    }

    private void recvRestart() {
        clearBoard();
        unlock();
    }

    private void sendRestart() {
        /** 5 to indicate that the game is cleared and restarted*/
        String message = "5\n";
        unlock();
    }

    private void recvStart(String data) {

    }

    private void sendStart() {
        /** 4 to indicate that the game has started*/
        String message = "4\n";

    }

    private void recvBoard(String data) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                valueGrid[i][j] = String.valueOf(data.charAt(i * 8 + j));

        otherPlayer = String.valueOf(data.charAt(64));

        switch (data.charAt(65)) {
            case 'l':
                lock();
                break;
            case 'u':
                unlock();
                break;
            default:
                break;
        }

        running = true;

        pushBoard();
    }

    private void sendBoard() {
        /** 3 to indicate that this is the current gamestate*/
        String message = "3\n";

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                message += valueGrid[i][j];

        message += localPlayer;

        if (lock) {
            message += 'u';
        } else {
            message += 'l';
        }

        network.send(ID, message);
    }

    private void recvCoord(String data) {
        String[] coords = data.split("\n", 3);
        setCoords(coords[0], coords[1], "O");
        unlock();
//        dataView.append("unlock from recvcoord");
    }

    private void sendCoord(String x, String y) {
        /** 1 to indicate that this is a coordinate */
        String message = "1\n";
        message += x + "\n" + y;
        network.send(ID, message);
        lock();
        dataView.append("lock from sendCoord");
    }

    private void recvWin(String data) {
        String[] coords = data.split("\n", 3);
        sendCoord(coords[0], coords[1]);
        //TODO YOU LOSE, lock, dialog(try again, quit game)
        dataView.setText("winrecv");
        lock();
    }

    private void sendWin(String x, String y) {
        /** 2 to indicate that this is a coordinate and the win condition*/
        String message = "2\n";
        message += x + "\n" + y;
        network.send(ID, message);
        //TODO YOU WIN, lock, dialog(try again, quit game)
        dataView.setText("win");
        lock();
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Helper Functions
     * ---------------------------------------------------------------------------------------------
     */

    private boolean localWin(String sx, String sy) {
        if(true)
        return false;
        int x = Integer.valueOf(sx);
        int y = Integer.valueOf(sy);
        int count = 1;
        for(int dx = -1; dx <= 1; dx++)
            for(int dy = -1; dy <= 1; dy++) {
                while (checkRow(x, y, dx, dy) && count < 4)
                    count++;
                int nx = dx * -1;
                int ny = dy * -1;
                while (checkRow(x, y, nx, ny) && count < 4)
                    count++;
                if(count >= 4) {
                    return true;
                } else count = 1;
            }



        //TODO check win
        return false;
    }

    private boolean checkRow(int x, int y, int dx, int dy) {
        if(x + dx < 8 && x + dx >=0)
            if(y + dy < 8 && y + dy >=0)
                if(valueGrid[x+dx][y+dy].equals(localPlayer))
                    return true;
        return false;

    }

    private boolean setCoords(String sx, String sy, String player) {
        int x = Integer.valueOf(sx);
        int y = Integer.valueOf(sy);

        if(!(valueGrid[x][y].equals(" "))) {
            //dataView.setText("Square already filled \"" + valueGrid[x][y] + "\"");
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                    dataView.append(valueGrid[i][j]);
            return false;
        }

        String text;

        if (player == "X")
            text = localPlayer;
        else if (player == "O")
            text = otherPlayer;
        else
            text = "E";

        ((Button) findViewById(idGrid[x][y])).setText(text);

        valueGrid[x][y] = player;
        return true;
    }

    private void setIdGrid() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                idGrid[i][j] = getResources().getIdentifier("b" + String.valueOf(i) + String.valueOf(j), "id", getPackageName());
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                ((Button) findViewById(idGrid[i][j])).setText(" ");
                valueGrid[i][j] = " ";
            }

    }

    private void pushBoard() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                ((Button) findViewById(idGrid[i][j])).setText(valueGrid[i][j]);
            }
    }

    @Override
    public void onPause() {
        network.unregisterGame(ID);
        super.onPause();
    }

    @Override
    public void onResume() {
        network.registerGame(ID, this);
        super.onResume();
    }

    private void unlock() {
        this.lock = false;
        //dataView.setText("your turn");
    }

    private void lock() {
        this.lock = true;
        //dataView.setText("his turn");
    }
}

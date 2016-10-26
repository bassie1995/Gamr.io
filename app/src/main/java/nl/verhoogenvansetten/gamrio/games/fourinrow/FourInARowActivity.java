package nl.verhoogenvansetten.gamrio.games.fourinrow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.network.Network;

public class FourInARowActivity extends GameCompat {

    private TextView dataView;
    private Network network;
    private int ID = 1;
    private int[][] idGrid = new int[8][8];
    private int[][] valueGrid = new int[8][8];

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
                network.send(ID, "helloooo");
                //dataView.setText(String.valueOf(network.getOtherGameID()));
                //dataView.setText(network.getConnectedDeviceName());
                //clearBoard();
            }
        });

        dataView = (TextView) findViewById(R.id.dataview);

        network = Network.getInstance();

        setIdGrid();
    }


    public void onClick(View v) {
        Button b = (Button) v;

        String sx = String.valueOf(b.getTag().toString().charAt(1));
        String sy = String.valueOf(b.getTag().toString().charAt(2));

        setCoords(sx, sy, localPlayer);

        //if (localWin(sx, sy))
            //sendWin(sx, sy);
        //else
            sendCoord(sx, sy);
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
            default:
                this.dataView.setText("Weird message received");

        }
        this.dataView.setText(data);
    }

    public void peerDown(){

    }

    public void peerUp(){

    }

    /**
     * Communication helper functions.
     */

    private void recvCoord(String data) {
        String[] coords = data.split("\n", 3);
        setCoords(coords[0], coords[1], otherPlayer);
    }

    private void sendCoord(String x, String y) {
        /** 1 to indicate that this is a coordinate */
        String message = "1\n";
        message += x + "\n" + y;
        network.send(ID, message);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Helper Functions
     * ---------------------------------------------------------------------------------------------
     */

    private void setCoords(String x, String y, String text) {
        ((Button) findViewById(idGrid[Integer.valueOf(x)][Integer.valueOf(y)])).setText(text);
    }

    private void setIdGrid() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                idGrid[i][j] = getResources().getIdentifier("b" + String.valueOf(i) + String.valueOf(j), "id", getPackageName());
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                ((Button) findViewById(idGrid[i][j])).setText("");

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

}

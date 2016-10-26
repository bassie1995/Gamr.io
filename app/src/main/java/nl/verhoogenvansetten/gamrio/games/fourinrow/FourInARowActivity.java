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

    TextView dataView;
    Network network;
    int ID = 11;
    int[][] grid = new int[8][8];

    int count = 0;

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

        setGrid();
    }


    public void onClick(View v) {
        Button b = (Button) v;
        int x, y;
        x = Integer.valueOf(String.valueOf(b.getTag().toString().charAt(1)));
        y = Integer.valueOf(String.valueOf(b.getTag().toString().charAt(2)));
        count++;
        dataView.setText("Count:" + count + " " + String.valueOf(x) + String.valueOf(y));
        //b.setText("x");
        Button bb = (Button) findViewById(getResources().getIdentifier("b" + String.valueOf(x) + String.valueOf(y), "id", getPackageName()));
        bb.setText("O");
    }

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

    private void recvCoord(String s) {

    }

    private void sendCoord(char x, int y) {
        //1 to indicate that this is a coordinate
        String message = "1\n";
        message += x + y;
        network.send(ID, message);
    }

    private void setGrid() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                grid[i][j] = getResources().getIdentifier("b" + String.valueOf(i) + String.valueOf(j), "id", getPackageName());
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                ((Button) findViewById(grid[i][j])).setText("");

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

package nl.verhoogenvansetten.gamrio;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.util.Network;
import nl.verhoogenvansetten.gamrio.util.Server;

public class FourInARowActivity extends GameCompat {

    TextView dataView;
    Network network;
    Server server;
    int ID = 11;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                network.send(ID, "helloooo");
            }
        });

        dataView = (TextView) findViewById(R.id.dataview);

        network = Network.getInstance();
    }

    public void update(String data) {
        this.dataView.setText(data);
    }

    @Override
    public void onPause() {
        network.unregisterGame();
        super.onPause();
    }

    @Override
    public void onResume() {
        network.registerGame(ID, this);
        super.onResume();
    }

}

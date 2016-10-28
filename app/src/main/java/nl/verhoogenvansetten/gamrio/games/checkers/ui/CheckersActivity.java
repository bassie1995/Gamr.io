package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.MessageUtil;
import nl.verhoogenvansetten.gamrio.util.network.Network;

/**
 * Created by Jori on 17-10-2016.
 *
 */

public class CheckersActivity extends GameCompat implements CheckersFragment.OnFragmentInteractionListener{

    TextView dataView;
    private Network network;
    private int ID = Network.CHECKERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout
        setContentView(R.layout.activity_checkers);

        //Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //todo implement networking
        network = Network.getInstance();

        //Now we add the fragment. Only on first boot.
        if(savedInstanceState == null){
            addFragment();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        network.registerGame(ID, this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        network.unregisterGame(ID);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void addFragment() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.fragment_container);
        getFragmentManager().beginTransaction().add(ll.getId(), CheckersFragment.newInstance(),
                CheckersFragment.TAG).commit();
    }


    public void update(String data) {
        this.dataView.setText(data);
    }

    @Override
    public void peerDown() {
        MessageUtil.showMessage(getApplicationContext(), "Peer down");
    }

    @Override
    public void peerUp() {
        MessageUtil.showMessage(getApplicationContext(), "Peer up");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

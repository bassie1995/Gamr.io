package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.Network;

/**
 * Created by Jori on 17-10-2016.
 *
 */

public class CheckersActivity extends GameCompat implements CheckersFragment.OnFragmentInteractionListener{

    TextView dataView;
    Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout
        setContentView(R.layout.activity_checkers);

        //Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set the button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //todo implement networking
//        network = Network.getInstance();
//        network.registerGame(7, this);

        //Edit Size of the LinearLayout holding the fragment to make it square
        makeSquare();

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
        super.onResume();
    }

    @Override
    protected void onPause() {
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
    public void onFragmentInteraction(Uri uri) {

    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void makeSquare(){
        LinearLayout ll = (LinearLayout) findViewById(R.id.fragment_container);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        ViewGroup.LayoutParams lp;
        if(width <= height){
            //If width is smaller then the height, asume the screen is vertical
            //Set the height to the same size as the width
            lp = new CoordinatorLayout.LayoutParams(width, width);
        }else{
            //Asume the screen is horizontal.
            //Set the width to the same size as the height.
            lp = new CoordinatorLayout.LayoutParams(height, height);
        }
        //Set the parameters
        ll.setLayoutParams(lp);
    }

}

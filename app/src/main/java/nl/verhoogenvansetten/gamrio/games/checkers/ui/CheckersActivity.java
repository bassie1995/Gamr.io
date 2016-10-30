package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.games.checkers.model.BoardPosition;
import nl.verhoogenvansetten.gamrio.games.checkers.model.Checkers;
import nl.verhoogenvansetten.gamrio.games.checkers.model.Side;
import nl.verhoogenvansetten.gamrio.util.network.Network;

/**
 * Created by Jori on 17-10-2016.
 *
 */

public class CheckersActivity extends GameCompat implements CheckersFragment.OnFragmentInteractionListener{

    private Network network;
    private int ID = Network.CHECKERS;
    //private int ID = 11;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if ((PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_checkers_enable", false) &&
                PreferenceManager.getDefaultSharedPreferences(this).getString("pref_checkers_theme_list", "light").equals("dark")) ||
                PreferenceManager.getDefaultSharedPreferences(this).getString("general_theme_list", "light").equals("dark"))
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        super.onCreate(savedInstanceState);

        //Set the layout
        setContentView(R.layout.activity_checkers);

        //Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        network = Network.getInstance();

        //Now we add the fragment. Only on first boot.
        if(savedInstanceState == null){
            addFragment();
        }
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
        finish();
    }

    private void addFragment() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.fragment_container);
        //if we aren't the host
        if(!network.isGroupOwner()) {
            getFragmentManager().beginTransaction().add(ll.getId(),
                    CheckersFragment.newInstance(Side.BLACK),
                    CheckersFragment.TAG).commit();
        }
        //If we are the host.
        else{
            getFragmentManager().beginTransaction().add(ll.getId(),
                    CheckersFragment.newInstance(Side.WHITE),
                    CheckersFragment.TAG).commit();
        }
    }


    public void update(String data) {

        //Get the checkers fragment
        CheckersFragment cf = (CheckersFragment) getFragmentManager().findFragmentByTag(
                CheckersFragment.TAG);

        //Decode the dataString to a temporary BoardPostion 2D array object
        BoardPosition[][] tempBoard = (BoardPosition[][])decodeSentDataToObject(data);

        //Replace the current pieces within the board with the newly received data
        for(int x = 0; x < 8; x++)
            for(int y = 0; y < 8; y++){
                cf.checkers.board[x][y].setPiece(tempBoard[x][y].getPiece());
            }

        //Set the turn
        if(cf.checkers.getTurn() == Side.BLACK)
            cf.checkers.setTurn(Side.WHITE);
        else
            cf.checkers.setTurn(Side.BLACK);
        //Update the gui
        cf.updateGUI();
    }

    @Override
    public void peerDown() {
    }

    @Override
    public void peerUp() {
    }


    @Override
    public void onSendData(Checkers checkers) {
        //Translate the checkers object to a byte array
        String encodedCheckersBoard = encodeDataToString(checkers.board);

        //Send the checkers object containing the current game
        if(network.send(ID, encodedCheckersBoard)){
        }
        else {
            Toast.makeText(this, "Something went wrong sending the game, try to restart the application", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeDataToString(Object object) {
        String data = null;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            data = new String(Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private Object decodeSentDataToObject(String data) {
        Object object = null;
        try {
            byte b[] = Base64.decode(data.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bis = new ByteArrayInputStream(b);
            ObjectInputStream ois = new ObjectInputStream(bis);
            object = ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void onEndGame() {
        network.unregisterGame(ID);
        this.finish();
    }
}

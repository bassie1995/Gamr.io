package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.os.Bundle;
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
import nl.verhoogenvansetten.gamrio.util.network.Network;

/**
 * Created by Jori on 17-10-2016.
 *
 */

public class CheckersActivity extends GameCompat implements CheckersFragment.OnFragmentInteractionListener{

    private Network network;
    //private int ID = Network.CHECKERS;
    private int ID = 11;

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
        getFragmentManager().beginTransaction().add(ll.getId(),
                CheckersFragment.newInstance(),
                CheckersFragment.TAG).commit();
    }


    public void update(String data) {
        Toast.makeText(this, "Data received: " + data, Toast.LENGTH_SHORT).show();

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

    }

    @Override
    public void peerDown() {
        Toast.makeText(this, "Peer down", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void peerUp() {
        Toast.makeText(this, "Peer up", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSendData(Checkers checkers) {
        //Translate the checkers object to a byte array
        String encodedCheckersBoard = encodeDataToString(checkers.board);

        //Send the checkers object containing the current game
        if(network.send(ID, encodedCheckersBoard)){
            Toast.makeText(this, "Send the game", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Something went wrong sending the game", Toast.LENGTH_SHORT).show();
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
    public void onEndGame(Checkers checkers) {

    }
}

package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.databinding.BattleshipBinding;
import nl.verhoogenvansetten.gamrio.games.battleship.model.Ship;
import nl.verhoogenvansetten.gamrio.util.network.Network;

public class BattleshipGameActivity extends GameCompat {
    BattleshipBinding binding;
    Ship[] mShips = new Ship[5];

    private Network network;
    private int ID = Network.BATTLESHIP;

    boolean mViewingOwnGrid = true;
    int mLastButtonId;
    int[] mOwnHits = new int[100];
    int mOwnHitIndex = 0;
    int[] mOwnMisses = new int[100];
    int mOwnMissIndex = 0;
    int[] mOpponentShots = new int[100];
    int mOpponentShotsIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battleship);
        setTitle("Battleship");
        binding = DataBindingUtil.setContentView(this, R.layout.battleship);

        network = Network.getInstance();

        setup();

    }

    private void setup() {
        Intent startIntent = getIntent();

        int[] carrier = startIntent.getIntArrayExtra(getString(R.string.carrier));
        int[] battleship = startIntent.getIntArrayExtra(getString(R.string.battleship));
        int[] cruiser = startIntent.getIntArrayExtra(getString(R.string.cruiser));
        int[] submarine = startIntent.getIntArrayExtra(getString(R.string.submarine));
        int[] destroyer = startIntent.getIntArrayExtra(getString(R.string.destroyer));
        boolean mDoneSecond = startIntent.getBooleanExtra(getString(R.string.opponent_done), true);

        mShips[0] = new Ship(5, getString(R.string.carrier));
        mShips[0].setButtons(carrier);
        mShips[1] = new Ship(4, getString(R.string.battleship));
        mShips[1].setButtons(battleship);
        mShips[2] = new Ship(3, getString(R.string.cruiser));
        mShips[2].setButtons(cruiser);
        mShips[3] = new Ship(3, getString(R.string.submarine));
        mShips[3].setButtons(submarine);
        mShips[4] = new Ship(2, getString(R.string.destroyer));
        mShips[4].setButtons(destroyer);

        if (mDoneSecond) {
            viewOpponentGrid();
        } else {
            viewOwnGrid();
        }
    }

    public void onClick(View v) {
        Button b = (Button) v;
        int buttonId = b.getId();
        if (mLastButtonId != buttonId) {
            network.send(ID, Integer.toString(buttonId));
            setGridClickable(false, buttonId);
            mLastButtonId = buttonId;
            b.setText("X");
        } else {
            setGridClickable(true, buttonId);
            mLastButtonId = -1;
            b.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_battleship_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_done_battleship:
                network.send(ID, Integer.toString(mLastButtonId));
                // Check for hit or miss and update UI accordingly, switch to other grid mode
                return true;
            case R.id.menu_flip_battleship:
                if (mViewingOwnGrid) viewOpponentGrid();
                else viewOwnGrid();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setGridClickable(boolean clickable, int exceptionId) {
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            if (b.getId() != exceptionId)
                b.setClickable(clickable);
        }
    }

    private void viewOwnGrid() {

    }

    private void viewOpponentGrid() {
        setGridClickable(false, -1);
        for (int id : mShips[0].getButtons())
            findViewById(id).getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        for (int id : mShips[1].getButtons())
            findViewById(id).getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        for (int id : mShips[2].getButtons())
            findViewById(id).getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        for (int id : mShips[3].getButtons())
            findViewById(id).getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        for (int id : mShips[4].getButtons())
            findViewById(id).getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);

        /*for (int id : mOpponentShots) {
            Button b = (Button) findViewById(id);
            b.setText("X");
        }*/
    }

    @Override
    public void update(String data) {
        /*int buttonId = Integer.parseInt(data);
        Button b = (Button) findViewById(buttonId);
        mOpponentShots[mOpponentShotsIndex++] = buttonId;
        b.setText("X");*/
    }

    @Override
    public void peerDown() {

    }

    @Override
    public void peerUp() {

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

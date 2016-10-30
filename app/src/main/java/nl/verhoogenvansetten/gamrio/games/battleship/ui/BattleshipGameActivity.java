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
import android.widget.Toast;

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

        setup();
    }

    private void setup() {
        network = Network.getInstance();
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
            startOpponentTurn();
        } else {
            startOwnTurn();
        }
    }

    public void onClick(View v) {
        Button b = (Button) v;
        int buttonId = b.getId();
        if (mLastButtonId != buttonId) {
            int[] exceptionArray = {buttonId};
            setGridClickable(false, exceptionArray);
            mLastButtonId = buttonId;
            b.setText("X");
        } else {
            int[] exceptionArray = {-1};
            setGridClickable(true, exceptionArray);
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
                Toast.makeText(this, Boolean.toString(mViewingOwnGrid), Toast.LENGTH_SHORT).show();
                if (mViewingOwnGrid) {
                    viewOpponentGrid();
                } else {
                    viewOwnGrid();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setGridClickable(boolean clickable, int[] exceptionIds) {
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.setClickable(clickable);
            for (int id : exceptionIds) {
                if (id == b.getId())
                    b.setClickable(!clickable);
            }
        }
    }

    private void startOwnTurn() {
        int[] exceptionArray = {-1};
        setGridClickable(true, exceptionArray);
        viewOwnGrid();

    }

    private void startOpponentTurn() {
        int[] exceptionArray = {-1};
        setGridClickable(false, exceptionArray);
        viewOpponentGrid();
    }

    private void viewOwnGrid() {
        mViewingOwnGrid = true;
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.getBackground().clearColorFilter();
            b.setText("");
        }
        for (int id : mOwnHits) {
            if (id != 0) {
                Button b = (Button) findViewById(id);
                b.getBackground().clearColorFilter();
                b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_green_a200), PorterDuff.Mode.MULTIPLY);
                b.setText("X");
            }
        }
        for (int id : mOwnMisses) {
            if (id != 0) {
                Button b = (Button) findViewById(id);
                b.getBackground().clearColorFilter();
                b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_red_a200), PorterDuff.Mode.MULTIPLY);
                b.setText("O");
            }
        }
    }

    private void viewOpponentGrid() {
        mViewingOwnGrid = false;
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.getBackground().clearColorFilter();
            b.setText("");
        }
        for (int id : mShips[0].getButtons()) {
            Button b = (Button) findViewById(id);
            b.setText("");
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        }
        for (int id : mShips[1].getButtons()) {
            Button b = (Button) findViewById(id);
            b.setText("");
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        }
        for (int id : mShips[2].getButtons()) {
            Button b = (Button) findViewById(id);
            b.setText("");
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        }
        for (int id : mShips[3].getButtons()) {
            Button b = (Button) findViewById(id);
            b.setText("");
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        }
        for (int id : mShips[4].getButtons()) {
            Button b = (Button) findViewById(id);
            b.setText("");
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
        }

        for (int id : mOpponentShots) {
            if (id != 0) {
                Button b = (Button) findViewById(id);
                b.setText("X");
            }
        }
    }

    @Override
    public void update(String data) {
        if ("miss".equals(data)) {
            Button b = (Button) findViewById(mLastButtonId);
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_red_a200), PorterDuff.Mode.MULTIPLY);
            b.setText("O");
            mOwnMisses[mOwnMissIndex++] = mLastButtonId;
            startOpponentTurn();
        } else if ("hit".equals(data)) {
            Button b = (Button) findViewById(mLastButtonId);
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_green_a200), PorterDuff.Mode.MULTIPLY);
            b.setText("X");
            mOwnHits[mOwnHitIndex++] = mLastButtonId;
            startOpponentTurn();
        } else {
            int buttonId = Integer.parseInt(data);
            Button b = (Button) findViewById(buttonId);
            boolean isHit = false;
            for (Ship ship : mShips)
                if (ship.isHit(buttonId))
                    isHit = true;
            if (isHit) network.send(ID, "hit");
            else network.send(ID, "miss");
            mOpponentShots[mOpponentShotsIndex++] = buttonId;
            b.setText("X");
            startOwnTurn();
        }
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

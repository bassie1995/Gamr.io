package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import static nl.verhoogenvansetten.gamrio.R.layout.battleship;

public class BattleshipSetupActivity extends GameCompat {
    BattleshipBinding binding;
    Ship[] mShips = new Ship[5];

    private Network network;
    private int ID = Network.BATTLESHIP;
    SharedPreferences prefs;

    int mCurrentShip;
    int mShipNum;
    boolean mSetupDone;
    boolean mOpponentSetupDone = false;
    private float size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ((PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_battleship_enable", false) &&
                PreferenceManager.getDefaultSharedPreferences(this).getString("pref_battleship_theme_list", "light").equals("dark")) ||
                PreferenceManager.getDefaultSharedPreferences(this).getString("general_theme_list", "light").equals("dark"))
            setTheme(R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(battleship);
        setTitle("Battleship setup");
        binding = DataBindingUtil.setContentView(this, battleship);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        network = Network.getInstance();

        mShips[0] = new Ship(5, getString(R.string.carrier));
        mShips[1] = new Ship(4, getString(R.string.battleship));
        mShips[2] = new Ship(3, getString(R.string.cruiser));
        mShips[3] = new Ship(3, getString(R.string.submarine));
        mShips[4] = new Ship(2, getString(R.string.destroyer));
        mCurrentShip = 0;
        mShipNum = 0;
    }

    public void onClick(View v) {
        int[] mCoords = mShips[mCurrentShip].getCoordinates();
        int[] mButtons = mShips[mCurrentShip].getButtons();
        int position = Integer.parseInt(getResources().getResourceEntryName(v.getId()).replaceAll("[\\D]", ""));

        if (!mSetupDone && isValidCoords(position, mCoords)) {
            Button b = (Button) v;
            mCoords[mShipNum] = position;
            mButtons[mShipNum] = b.getId();
            mShipNum++;

            b.setClickable(false);
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
            mShips[mCurrentShip].setCoordinates(mCoords);
            mShips[mCurrentShip].setButtons(mButtons);

            if (atLastShip() && currentShipSetupDone()) {
                mShipNum = 0;
                mCurrentShip = 0;
                mSetupDone = true;
                Toast.makeText(this, getString(R.string.done_battleship), Toast.LENGTH_LONG).show();
            }
            if (currentShipSetupDone()) {
                Toast.makeText(this, mShips[mCurrentShip].getName() + " done", Toast.LENGTH_SHORT).show();
                mShipNum = 0;
                mCurrentShip++;
            }
        }
    }

    private boolean isValidCoords(int pos, int[] coords) {
        if (mShipNum == 0) return true;
        else if (mShipNum == 1) return (pos == coords[mShipNum - 1] - 10 || pos == coords[mShipNum - 1] + 10 || pos == coords[mShipNum - 1] - 1 || pos == coords[mShipNum - 1] + 1);
        else if (coords[mShipNum - 2] + 2 == pos && coords[mShipNum - 1] + 1 == pos) return true;
        else if (coords[mShipNum - 2] - 1 == pos && coords[mShipNum - 1] - 2 == pos) return true;
        else if (coords[mShipNum - 2] + 1 == pos && coords[mShipNum - 1] + 2 == pos) return true;
        else if (coords[mShipNum - 2] - 2 == pos && coords[mShipNum - 1] - 1 == pos) return true;
        else if (coords[mShipNum - 2] + 20 == pos && coords[mShipNum - 1] + 10 == pos) return true;
        else if (coords[mShipNum - 2] - 10 == pos && coords[mShipNum - 1] - 20 == pos) return true;
        else if (coords[mShipNum - 2] - 20 == pos && coords[mShipNum - 1] - 10 == pos) return true;
        else if (coords[mShipNum - 2] + 10 == pos && coords[mShipNum - 1] + 20 == pos) return true;
        else return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_battleship_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_play_battleship:
                if (mSetupDone) startBattleshipGame();
                else Toast.makeText(this, R.string.should_finish_setup_battleship, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_clear_all_battleship:
                clearGrid();
                return true;
            case R.id.menu_clear_battleship:
                clearCurrentShip();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean currentShipSetupDone() {
        return mShipNum == mShips[mCurrentShip].getLength();
    }

    private boolean atLastShip() {
        return mCurrentShip == mShips.length-1;
    }

    private void clearCurrentShip() {
        for (int id : mShips[mCurrentShip].getButtons()) {
            if (id != 0) {
                Button b = (Button) findViewById(id);
                b.getBackground().clearColorFilter();
                b.setClickable(true);
            }
        }
        mShipNum = 0;
        mSetupDone = false;
    }

    private void clearGrid() {
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.setText("");
            b.getBackground().clearColorFilter();
            b.setClickable(true);
        }
        mCurrentShip = 0;
        mShipNum = 0;
        mSetupDone = false;
    }

    private void startBattleshipGame() {
        if (!mOpponentSetupDone) network.send(ID, "done");
        Intent intent = new Intent(BattleshipSetupActivity.this, BattleshipGameActivity.class);
        intent.putExtra(getString(R.string.carrier), mShips[0].getButtons());
        intent.putExtra(getString(R.string.battleship), mShips[1].getButtons());
        intent.putExtra(getString(R.string.cruiser), mShips[2].getButtons());
        intent.putExtra(getString(R.string.submarine), mShips[3].getButtons());
        intent.putExtra(getString(R.string.destroyer), mShips[4].getButtons());
        intent.putExtra(getString(R.string.opponent_done), mOpponentSetupDone);
        startActivity(intent);
    }

    @Override
    public void update(String data) {
        Toast.makeText(this, R.string.other_player_done, Toast.LENGTH_SHORT).show();
        if (data.contains("done"))
            mOpponentSetupDone = true;
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

        String color;
        if (prefs.getBoolean("pref_battleship_enable", false))
            color = prefs.getString("pref_battleship_text_color_list", "black");
        else
            color = prefs.getString("general_text_color_list", "black");
        switch (color) {
            case "black":
                setColor(ContextCompat.getColor(this, R.color.md_black));
                break;
            case "yellow":
                setColor(ContextCompat.getColor(this, R.color.md_yellow_500));
                break;
            default:
                setColor(ContextCompat.getColor(this, R.color.md_red_500));
                break;
        }

        if (prefs.getBoolean("pref_battleship_enable", false))
            setSize(Float.parseFloat(prefs.getString("pref_battleship_text_size_list", "14")));
        else
            setSize(Float.parseFloat(prefs.getString("general_text_size_list", "14")));
    }

    public void setColor(int color) {
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.setTextColor(color);
        }
    }

    public void setSize(float size) {
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.setTextSize(size);
        }
    }
}

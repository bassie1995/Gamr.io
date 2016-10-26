package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.databinding.BattleshipBinding;
import nl.verhoogenvansetten.gamrio.games.battleship.model.Ship;

import static nl.verhoogenvansetten.gamrio.R.layout.battleship;

public class BattleshipSetupActivity extends AppCompatActivity {
    BattleshipBinding binding;
    Ship[] mShips = new Ship[5];
    int mCurrentShip;
    int mShipNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(battleship);
        setTitle("Battleship setup");
        binding = DataBindingUtil.setContentView(this, battleship);

        mShips[0] = new Ship(5);
        mShips[1] = new Ship(4);
        mShips[2] = new Ship(3);
        mShips[3] = new Ship(3);
        mShips[4] = new Ship(2);
        mCurrentShip = 0;
        mShipNum = 0;
    }

    public void onClick(View v) {
        Button b = (Button) v;
        int[] mCoords = mShips[mCurrentShip].getCoordinates();
        int position = Integer.parseInt(getResources().getResourceEntryName(v.getId()).replaceAll("[\\D]", ""));

        if (isValidCoords(position, mCoords)) {
            int[] mNewCoords = mCoords;
            mNewCoords[mShipNum++] = position;

            b.setClickable(false);
            b.setText("X");
            b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_light_blue_a200), PorterDuff.Mode.MULTIPLY);
            mShips[mCurrentShip].setCoordinates(mNewCoords);
        }
        Toast.makeText(this, Arrays.toString(mShips), Toast.LENGTH_SHORT).show();


    }

    private boolean isValidCoords(int pos, int[] coords) {
        if (mShipNum == 0) return true;
        else if (mShipNum == 1)
            return (coords[mShipNum - 1] - 10 == pos || coords[mShipNum - 1] + 10 == pos || coords[mShipNum - 1] - 1 == pos || coords[mShipNum - 1] + 1 == pos);
        else
            return ((coords[mShipNum - 2] - 10 == pos || coords[mShipNum - 2] + 10 == pos) && coords[mShipNum - 1] - 10 == pos || coords[mShipNum - 1] + 10 == pos) ||
                    ((coords[mShipNum - 2] - 1 == pos || coords[mShipNum - 2] + 1 == pos) && coords[mShipNum - 1] - 1 == pos || coords[mShipNum - 1] + 1 == pos);
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
                startActivity(new Intent(this, BattleshipGameActivity.class));
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

    public void clearCurrentShip() {

    }

    public void clearGrid() {
        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            b.setText("");
            b.getBackground().clearColorFilter();
            b.setClickable(true);
        }
        mCurrentShip = 0;
    }

    public int min(int[] coords){
        int min = 100;
        for (int i : coords)
            if (i < min)
                min = i;
        return min;
    }

    public int max(int[] coords){
        int max = 0;
        for (int i : coords)
            if (i > max)
                max = i;
        return max;
    }
}

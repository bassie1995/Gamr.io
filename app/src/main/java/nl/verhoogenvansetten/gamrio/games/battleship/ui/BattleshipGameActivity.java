package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.databinding.ActivityBattleshipGameBinding;

public class BattleshipGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battleship_game);
        setTitle("Battleship");
        ActivityBattleshipGameBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_battleship_game);

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
                // Send data to other device
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

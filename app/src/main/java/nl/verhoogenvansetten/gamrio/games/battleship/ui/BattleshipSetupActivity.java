package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.databinding.ActivityBattleshipSetupBinding;

public class BattleshipSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battleship_setup);
        setTitle("Battleship setup");
        ActivityBattleshipSetupBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_battleship_setup);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

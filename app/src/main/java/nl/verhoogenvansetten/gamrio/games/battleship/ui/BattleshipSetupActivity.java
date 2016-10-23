package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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
}

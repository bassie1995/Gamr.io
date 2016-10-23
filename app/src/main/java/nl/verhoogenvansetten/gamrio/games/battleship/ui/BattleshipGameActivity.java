package nl.verhoogenvansetten.gamrio.games.battleship.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
}

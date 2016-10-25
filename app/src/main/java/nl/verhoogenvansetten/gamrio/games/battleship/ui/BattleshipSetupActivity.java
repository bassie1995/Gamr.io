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
import android.widget.LinearLayout;
import android.widget.Toast;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.databinding.ActivityBattleshipSetupBinding;

public class BattleshipSetupActivity extends AppCompatActivity {
    ActivityBattleshipSetupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battleship_setup);
        setTitle("Battleship setup");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battleship_setup);

    }

    public void onClick(View v) {
        Button button = (Button) v;

        button.setText("X");
        button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_amber_100), PorterDuff.Mode.MULTIPLY);
        //button.setBackgroundColor(ContextCompat.getColor(this, R.color.md_amber_50));
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

    public void onResume() {
        super.onResume();

        for (int i = 0; i < binding.battleshipGrid.getChildCount(); i++) {
            Button b = (Button) binding.battleshipGrid.getChildAt(i);
            //b.setBackgroundColor(ContextCompat.getColor(this, R.color.md_amber_50));
            b.setText("O");
        }
    }
}

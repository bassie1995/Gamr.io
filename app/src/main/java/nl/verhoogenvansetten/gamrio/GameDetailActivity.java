package nl.verhoogenvansetten.gamrio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import nl.verhoogenvansetten.gamrio.ui.HighScoreActivity;
import nl.verhoogenvansetten.gamrio.util.network.Network;

/**
 * An activity representing a single Game detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link GameListActivity}.
 */
public class GameDetailActivity extends AppCompatActivity {
    Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        network = Network.getInstance();

        ((ImageView) findViewById(R.id.iv_toolbar_image)).setImageResource(GameList.getGame(getIntent().getIntExtra(GameDetailFragment.ARG_ITEM_ID, -1)).image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivity(new Intent(GameDetailActivity.this, GameList.getGame(getIntent().getIntExtra(GameDetailFragment.ARG_ITEM_ID, -1)).className));
            }
        });

        //Create a fab for the highscores JD
        FloatingActionButton highscoresFab = (FloatingActionButton) findViewById(R.id.highscores_fab);
        if (network.getOtherGameID() == getIntent().getIntExtra(GameDetailFragment.ARG_ITEM_ID, -1))
            highscoresFab.setBackgroundColor(ContextCompat.getColor(this, R.color.md_green_a200));
        else
            highscoresFab.setBackgroundColor(ContextCompat.getColor(this, R.color.md_red_a200));
        highscoresFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameDetailActivity.this, HighScoreActivity.class);
                intent.putExtra("ID", getIntent().getIntExtra(GameDetailFragment.ARG_ITEM_ID, -1));
                startActivity(intent);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(GameDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(GameDetailFragment.ARG_ITEM_ID, -1));
            GameDetailFragment fragment = new GameDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.game_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, GameListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

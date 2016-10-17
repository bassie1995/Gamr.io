package nl.verhoogenvansetten.gamrio;

import android.support.v7.app.AppCompatActivity;

/**
 * This is the superclass for all the game activuties.
 */

public abstract class GameCompat extends AppCompatActivity {
    public abstract void update(String data);
}

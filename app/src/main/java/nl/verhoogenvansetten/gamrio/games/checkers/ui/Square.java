package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Jori on 17-10-2016.
 *
 */

public class Square extends ImageButton implements OnClickListener {

    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Square(Context context, int color) {
        super(context);
        this.color = color;
    }

    @Override
    public void onClick(View view) {
    }
}
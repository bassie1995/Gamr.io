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
    private int posX = -1;
    private int posY = -1;

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Square(Context context, int color, int posX, int posY) {
        super(context);
        this.color = color;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void onClick(View view) {
    }
}
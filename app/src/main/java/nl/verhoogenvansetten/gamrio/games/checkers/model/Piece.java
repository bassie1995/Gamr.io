package nl.verhoogenvansetten.gamrio.games.checkers.model;

import java.io.Serializable;

/**
 * Created by Jori on 13-10-2016.
 */

 public class Piece implements Serializable{

    private boolean crowned = false;
    private Side side = null;
    int posX = -1;
    int posY = -1;

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean isCrowned() {
        return crowned;
    }

    public void setCrowned(boolean crowned) {
        this.crowned = crowned;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public Piece(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    void crown(){
        this.crowned = true;
    }

}



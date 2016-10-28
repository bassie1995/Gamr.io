package nl.verhoogenvansetten.gamrio.games.checkers.model;

import java.io.Serializable;

/**
 * Created by Jori on 13-10-2016.
 */

public class BlackPiece extends Piece implements Serializable{
    public BlackPiece(int posX, int posY) {
        super(posX, posY);
        this.setSide(Side.BLACK);
    }
}

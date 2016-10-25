package nl.verhoogenvansetten.gamrio.games.checkers.model;

/**
 * Created by Jori on 13-10-2016.
 */

public class WhitePiece extends Piece {
    public WhitePiece(int posX, int posY) {
        super(posX, posY);
        this.setSide(Side.WHITE);
    }
}

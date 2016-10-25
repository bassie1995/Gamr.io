package nl.verhoogenvansetten.gamrio.games.checkers.model;

import nl.verhoogenvansetten.gamrio.games.checkers.ui.Square;

/**
 * Created by Jori on 24-10-2016.
 */

public class BoardPosition {
    Piece piece = null;
    Square square;


    public BoardPosition(Piece piece, Square square) {
        this.square = square;
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }
}

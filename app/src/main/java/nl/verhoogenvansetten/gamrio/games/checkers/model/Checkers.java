package nl.verhoogenvansetten.gamrio.games.checkers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import nl.verhoogenvansetten.gamrio.games.checkers.ui.Square;
import nl.verhoogenvansetten.gamrio.model.Game;

/**
 * Created by Jori on 13-10-2016.
 *
 */

public class Checkers implements Serializable {

    private Side turn;
    public BoardPosition[][] board;

    public Checkers() {
        //Instantiate the variables
        turn = Side.BLACK;
        //Init the board variable
        board = new BoardPosition[8][8];
        //Init all the spaces on the board
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if (board[x][y] == null){
                    board[x][y] = new BoardPosition(null, null);
                }
            }
        }
    }



    void nextTurn(){
        if(this.turn == Side.BLACK){
            this.turn = Side.WHITE;
        } else{
            this.turn = Side.BLACK;
        }
    }

    boolean blackWon(){
        //White has no pieces left
        return true;
    }

    boolean whiteWon(){
        //Black has no pieces left
        return true;
    }

    Side getWinningSide(){
        //Returns the winning side if applicable. Returns null if there's not a winner yet.
        boolean whiteWon = true;
        boolean blackWon = true;
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if(board[x][y].piece != null){
                    Piece piece = board[x][y].piece;
                    if (piece.canStep(board) || piece.canJump(board)){
                        if(piece.getSide() == Side.WHITE){
                            blackWon = false;
                        } else if (piece.getSide() == Side.BLACK){
                            whiteWon = false;
                        }
                    }
                }
            }
        }
        if(whiteWon){
            return Side.WHITE;
        }
        else if (blackWon){
            return Side.BLACK;
        }
        else{
            return null;
        }
    }

    public void setUpBoard(boolean newGame){


        if(newGame) {
            // place the white pieces
            board[0][1].piece = new WhitePiece();
            board[0][3].piece = new WhitePiece();
            board[0][5].piece = new WhitePiece();
            board[0][7].piece = new WhitePiece();

            board[1][0].piece = new WhitePiece();
            board[1][2].piece = new WhitePiece();
            board[1][4].piece = new WhitePiece();
            board[1][6].piece = new WhitePiece();

            board[2][1].piece = new WhitePiece();
            board[2][3].piece = new WhitePiece();
            board[2][5].piece = new WhitePiece();
            board[2][7].piece = new WhitePiece();

            //Place the black pieces
            board[7][0].piece = new WhitePiece();
            board[7][2].piece = new WhitePiece();
            board[7][4].piece = new WhitePiece();
            board[7][6].piece = new WhitePiece();

            board[6][1].piece = new WhitePiece();
            board[6][3].piece = new WhitePiece();
            board[6][5].piece = new WhitePiece();
            board[6][7].piece = new WhitePiece();

            board[5][0].piece = new WhitePiece();
            board[5][2].piece = new WhitePiece();
            board[5][4].piece = new WhitePiece();
            board[5][6].piece = new WhitePiece();
        }
    }

    private boolean movesLeft(){
        return true;
    }


}

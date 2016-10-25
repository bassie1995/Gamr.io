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

    public void setUpBoard(){
        // place the white pieces
        board[1][0].piece = new WhitePiece(1, 0);
        board[3][0].piece = new WhitePiece(3, 0);
        board[5][0].piece = new WhitePiece(5, 0);
        board[7][0].piece = new WhitePiece(7, 0);
        board[0][1].piece = new WhitePiece(0, 1);
        board[2][1].piece = new WhitePiece(2, 1);
        board[4][1].piece = new WhitePiece(4, 1);
        board[6][1].piece = new WhitePiece(6, 1);
        board[1][2].piece = new WhitePiece(1, 2);
        board[3][2].piece = new WhitePiece(3, 2);
        board[5][2].piece = new WhitePiece(5, 2);
        board[7][2].piece = new WhitePiece(7, 2);

        //Place the black pieces
        board[0][7].piece = new BlackPiece(0, 7);
        board[2][7].piece = new BlackPiece(2, 7);
        board[4][7].piece = new BlackPiece(4, 7);
        board[6][7].piece = new BlackPiece(6, 7);
        board[1][6].piece = new BlackPiece(1, 6);
        board[3][6].piece = new BlackPiece(3, 6);
        board[5][6].piece = new BlackPiece(5, 6);
        board[7][6].piece = new BlackPiece(7, 6);
        board[0][5].piece = new BlackPiece(0, 5);
        board[2][5].piece = new BlackPiece(2, 5);
        board[4][5].piece = new BlackPiece(4, 5);
        board[6][5].piece = new BlackPiece(6, 5);
    }

    private boolean movesLeft(){
        return true;
    }


}

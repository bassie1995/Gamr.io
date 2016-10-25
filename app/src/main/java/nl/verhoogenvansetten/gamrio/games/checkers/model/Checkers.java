package nl.verhoogenvansetten.gamrio.games.checkers.model;

import nl.verhoogenvansetten.gamrio.model.Game;

/**
 * Created by Jori on 13-10-2016.
 *
 */

public class Checkers extends Game {

    private Side turn = Side.BLACK;
    private Piece[][] board;

    public Checkers(int id, Class<?> className, int image, String name, String details) {
        super(id, className, image, name, details);
        setUpBoard();
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
                if(board[x][y] != null){
                    Piece piece = board[x][y];
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

    private void setUpBoard(){

        board = new Piece[8][8];

        //Init all the spaces on the board with null
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                board[x][y] = null;
            }
        }
        // place the white pieces
        board[0][1] = new WhitePiece();
        board[0][3] = new WhitePiece();
        board[0][5] = new WhitePiece();
        board[0][7] = new WhitePiece();

        board[1][0] = new WhitePiece();
        board[1][2] = new WhitePiece();
        board[1][4] = new WhitePiece();
        board[1][6] = new WhitePiece();

        board[2][1] = new WhitePiece();
        board[2][3] = new WhitePiece();
        board[2][5] = new WhitePiece();
        board[2][7] = new WhitePiece();

        //Place the black pieces
        board[7][0] = new WhitePiece();
        board[7][2] = new WhitePiece();
        board[7][4] = new WhitePiece();
        board[7][6] = new WhitePiece();

        board[6][1] = new WhitePiece();
        board[6][3] = new WhitePiece();
        board[6][5] = new WhitePiece();
        board[6][7] = new WhitePiece();

        board[5][0] = new WhitePiece();
        board[5][2] = new WhitePiece();
        board[5][4] = new WhitePiece();
        board[5][6] = new WhitePiece();
    }

    private boolean movesLeft(){
        return true;
    }


}

package nl.verhoogenvansetten.gamrio.games.checkers.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import nl.verhoogenvansetten.gamrio.model.Score;
import nl.verhoogenvansetten.gamrio.util.HighScoreUtil;
import nl.verhoogenvansetten.gamrio.util.network.Network;

/**
 * Created by Jori on 13-10-2016.
 *
 */

public class Checkers implements Serializable {
    private  Context context;

    public void setTurn(Side turn) {
        this.turn = turn;
    }

    private Side turn;
    private Side ourSide;
    public BoardPosition[][] board;
    private int score;
    private Piece selectedPiece = null;
    private int availableMoves;

    public void setScore(int score) {
        this.score = score;
    }

    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public void setAvailableMoves(int availableMoves) {
        this.availableMoves = availableMoves;
    }

    public Side getTurn() {
        return turn;

    }

    public int getScore() {
        return score;
    }

    public int getAvailableMoves() {
        return availableMoves;
    }


    //Interface for callback functions
    public interface CheckersListener {
        public void onUpdateGUI();
        public void onSendData();
        public void onEndGame();
    }
    //Listener
    private CheckersListener listener;

    public void setListener(CheckersListener listener) {
        this.listener = listener;
    }

    public Checkers(Context context, Side side) {
        //Instantiate the variables
        this.listener = null;
        this.context = context;
        //todo make preference setting
        this.turn = Side.BLACK;
        //Set our side
        ourSide = side;
        this.availableMoves = 1;
        //Default score
        this.score = 10000;
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

    public void turn(int posX, int posY){
        //if our turn
        if(this.turn == this.ourSide){
            //Check if there are steps or jumps available for this side
            if(jumpsAvailAble(ourSide) || stepsAvailable(ourSide)){
                //if the piece is selected
                if(this.selectedPiece != null){
                    //If we can jump to the currently selected boardposition
                    if(canJumpToPos(this.selectedPiece, posX, posY)){
                        //Make the jump
                        jump(this.selectedPiece, posX, posY);
                        // if there are more jumps available for this piece
                        if(jumpsAvailableForPiece(this.selectedPiece)){
                            updateGUI();
                        }
                        //There are no jumps available for this piece
                        else{
                            //Save the piece
                            savePiece(selectedPiece);
                            //Deselect the piece
                            this.selectedPiece = null;
                            //Set the availableMoves to zero. End of turn
                            this.availableMoves = 0;
                            //Update the interface
                            this.updateGUI();
                        }
                    }
                    //If we cant jump to the selected boardposition
                    else {
                        //Check if can jump with one of our pieces
                        if(jumpsAvailAble(this.ourSide)){
                            Toast.makeText(context, "Jumps are available", Toast.LENGTH_SHORT).show();
                            //Save the piece
                            savePiece(this.selectedPiece);
                            //Deselect the piece
                            this.selectedPiece = null;
                            //Update the interface
                            this.updateGUI();
                        }
                        //But if we can't jump with one of our other pieces
                        else{
                            //Check if the piece can step to a position
                            if (stepsAvailableForPiece(selectedPiece)){
                                //Check if we can step to the selected boardposition
                                if (canStepToPos(this.selectedPiece, posX, posY)){
                                    //When we can, step to the selected boardposition
                                    step(this.selectedPiece, posX, posY);
                                }
                                //We can't step to that position
                                else{
                                    Toast.makeText(context, "Invalid step", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //No steps available for piece
                            else{
                                //Inform player
                                Toast.makeText(context, "No steps available for this piece", Toast.LENGTH_SHORT).show();
                                //Save the piece
                                savePiece(selectedPiece);
                                //Deselect the piece
                                this.selectedPiece = null;
                                //Update the interface
                                this.updateGUI();
                            }
                        }
                    }
                }
                //No piece has been selected yet
                else{
                    //Check if the currently selected boardposition has a piece
                    if(positionHasPiece(posX, posY)){
                        //Check if the piece is of our side
                        if(ourPiece(this.ourSide, posX, posY)){
                            //Select the piece
                            this.selectedPiece = board[posX][posY].getPiece();
                            //Update the interface
                            this.updateGUI();
                        }
                        //The piece is not on our side
                        else{
                            //Inform player
                            Toast.makeText(context, "Please select a piece of your own side", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //There is not piece at the selected boardposition
                    else{
                        //Inform player
                        Toast.makeText(context, "Please select a piece", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //If there are no more jumps or steps available for this side the turn is over
            else{
                this.availableMoves = 0;
            }
        }
        //If it's not our turn
        else{
            //Inform the player
            Toast.makeText(context, "Wait for your turn", Toast.LENGTH_SHORT).show();
        }

        //At the end of every turn check if there are no more moves
        if(this.availableMoves == 0){
            //Check if there is a winner
            if(getWinningSide() != null){
                //If there is a winner, inform the player.
                Toast.makeText(context, getWinningSide().toString() + " is the winner with the score of " + this.score, Toast.LENGTH_SHORT).show();
                //Add the score to the highscores when the player is the winner
                if(getWinningSide() == ourSide){
                    //todo Abillity to enter name for score + add score to highscores
                    //showHighScoreDialog();
                    //Give the other player the turn so that it get's the winner result as well
                    nextTurn();
                }
                //End the game
                endGame();
            }
            //If there is no winner
            else{
                nextTurn();
            }
        }
    }

    private void showHighScoreDialog() {
        //todo wip highscoredialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Score score = new Score();
                score.setPlayerName(input.getText().toString());
                score.setPoints(getScore());
                HighScoreUtil.addScore(Network.CHECKERS, score);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean stepsAvailableForPiece(Piece selectedPiece) {
        boolean stepsAvailable = false;
        if (canStepToPos(selectedPiece, selectedPiece.posX+1, selectedPiece.posY +1))
            stepsAvailable = true;
        else if (canStepToPos(selectedPiece, selectedPiece.posX+1, selectedPiece.posY -1))
            stepsAvailable = true;
        else if (canStepToPos(selectedPiece, selectedPiece.posX-1, selectedPiece.posY +1))
            stepsAvailable = true;
        else if (canStepToPos(selectedPiece, selectedPiece.posX-1, selectedPiece.posY -1))
            stepsAvailable = true;

        return stepsAvailable;
    }

    private void savePiece(Piece piece) {
        board[piece.posX][piece.posY].setPiece(piece);
    }

    private boolean ourPiece(Side ourSide, int x, int y) {
        if(board[x][y].piece.getSide() == ourSide)
            return true;
        else
            return false;
    }

    private boolean positionHasPiece(int x, int y) {
        if(board[x][y].getPiece() != null)
            return true;
        else
            return false;
    }

    private void step(Piece selectedPiece, int x, int y) {
        //Move the piece and set the new position
        this.selectedPiece = movePiece(selectedPiece, x, y);
        //Check if the piece needs to be crowned
        if (onOpponentBackline(this.ourSide, selectedPiece)){
            //Crown piece
            selectedPiece.crown();
        }
        //Piece selected false
        this.selectedPiece = null;
        //End the turn
        this.availableMoves = 0;
        updateGUI();
    }

    private boolean canStepToPos(Piece selectedPiece, int x, int y) {
        boolean canStep = false;
        //Check for out of bounds values
        if(x >= 0 && x <= 7 && y >= 0 && y <= 7 ){
            //Check if the boardPosition is empty
            if(boardPositionIsEmpty(x,y)) {
                //If we can step depends on the side of the piece
                if (selectedPiece.getSide() == Side.BLACK) {
                    //It also depends on if the selected piece is crowned
                    if (selectedPiece.isCrowned()) {
                        // 1 smaller or 1 greater then posX AND 1 smaller or 1 greater then posY
                        if (    (x == selectedPiece.posX + 1 || x == selectedPiece.posX - 1) &&
                                (y == selectedPiece.posY + 1 || y == selectedPiece.posY - 1))
                            canStep = true;
                    }
                    //If not crowned
                    else {
                        // 1 smaller or 1 greater then posX AND 1 smaller posY
                        if (    (x == selectedPiece.posX + 1 || x == selectedPiece.posX - 1) &&
                                (y == selectedPiece.posY - 1))
                            canStep = true;
                    }
                } else {
                    //It also depends on if the selected piece is crowned
                    if (selectedPiece.isCrowned()) {
                        // 1 smaller or 1 greater then posX AND 1 smaller or 1 greater then posY
                        if (    (x == selectedPiece.posX + 1 || x == selectedPiece.posX - 1) &&
                                (y == selectedPiece.posY + 1 || y == selectedPiece.posY - 1))
                            canStep = true;
                    }
                    //If not crowned
                    else {
                        // 1 smaller or 1 greater then posX AND 1 greater posY
                        if (    (x == selectedPiece.posX + 1 || x == selectedPiece.posX - 1) &&
                                (y == selectedPiece.posY + 1))
                            canStep = true;
                    }
                }
            }
        }
        return canStep;
    }

    private boolean boardPositionIsEmpty(int x, int y) {
        if(this.board[x][y].getPiece() == null)
            return true;
        else
            return false;
    }

    private boolean jumpsAvailableForPiece(Piece selectedPiece) {
        boolean jumpsAvailable = false;
        if (canJumpToPos(selectedPiece, selectedPiece.posX+2, selectedPiece.posY +2))
            jumpsAvailable = true;
        else if (canJumpToPos(selectedPiece, selectedPiece.posX+2, selectedPiece.posY -2))
            jumpsAvailable = true;
        else if (canJumpToPos(selectedPiece, selectedPiece.posX-2, selectedPiece.posY +2))
            jumpsAvailable = true;
        else if (canJumpToPos(selectedPiece, selectedPiece.posX-2, selectedPiece.posY -2))
            jumpsAvailable = true;

        return jumpsAvailable;
    }

    private void jump(Piece selectedPiece, int x, int y) {
        //Remove the jumped piece
        removeJumpedPiece(selectedPiece, x, y);
        //Move the piece and set the new position
        selectedPiece = movePiece(selectedPiece, x, y);
        //Check if the piece needs to be crowned
        if (onOpponentBackline(this.ourSide, selectedPiece)){
            //Crown piece
            selectedPiece.crown();
        }
        updateGUI();
    }

    private void removeJumpedPiece(Piece originalPieceLocation, int x, int y) {
        //Location of the jumped piece
        int pieceLocX;
        int pieceLocY;

        //Calculate the location of the jumped piece
        if(x < selectedPiece.posX && y < selectedPiece.posY){
            pieceLocX = selectedPiece.posX -1;
            pieceLocY = selectedPiece.posY -1;
        }
        else if(x < selectedPiece.posX && y > selectedPiece.posY){
            pieceLocX = selectedPiece.posX -1;
            pieceLocY = selectedPiece.posY +1;
        }
        else if(x > selectedPiece.posX && y < selectedPiece.posY){
            pieceLocX = selectedPiece.posX +1;
            pieceLocY = selectedPiece.posY -1;
        }
        else {
            pieceLocX = selectedPiece.posX +1;
            pieceLocY = selectedPiece.posY +1;
        }

        //remove the piece
        board[pieceLocX][pieceLocY].setPiece(null);
    }

    private boolean onOpponentBackline(Side ourSide, Piece selectedPiece) {
        //The backline depends on side
        if(ourSide == Side.BLACK) {
            //For the black side the backline is Y 0
            if (selectedPiece.posY == 0)
                return true;
        }else{
            //For the white side the backline is X 0
            if(selectedPiece.posY == 7)
                return true;
        }
        return false;
    }

    private Piece movePiece(Piece selectedPiece, int x, int y) {
        int tempX = selectedPiece.posX;
        int tempY = selectedPiece.posY;
        //Set the piece in the new position
        selectedPiece.posX = x;
        selectedPiece.posY = y;
        board[x][y].setPiece(selectedPiece);
        //Remove the piece from the old position;
        board[tempX][tempY].setPiece(null);
        return selectedPiece;
    }

    private boolean canJumpToPos(Piece selectedPiece, int x, int y) {
        boolean canJump = false;
        //pieceLocation of the to be jumped piece
        int pieceLocX;
        int pieceLocY;
        //Get the side of the piece
        Side side = selectedPiece.getSide();
        //Check for out of bounds values
        if(x >= 0 && x <= 7 && y >= 0 && y <= 7 ){
            //The position being jumped to has to be empty
            if(boardPositionIsEmpty(x, y)){
                //Calculate the location of the to be jumped piece
                if(x < selectedPiece.posX && y < selectedPiece.posY){
                    pieceLocX = selectedPiece.posX -1;
                    pieceLocY = selectedPiece.posY -1;
                }
                else if(x < selectedPiece.posX && y > selectedPiece.posY){
                    pieceLocX = selectedPiece.posX -1;
                    pieceLocY = selectedPiece.posY +1;
                }
                else if(x > selectedPiece.posX && y < selectedPiece.posY){
                    pieceLocX = selectedPiece.posX +1;
                    pieceLocY = selectedPiece.posY -1;
                }
                else {
                    pieceLocX = selectedPiece.posX +1;
                    pieceLocY = selectedPiece.posY +1;
                }
                //check if the pieceLocation isnt out of bounds
                if(pieceLocX >= 0 && pieceLocX <= 7 && pieceLocY >=0 && pieceLocY <= 7){
                    //check if there is a piece between the current position and the target
                    if(positionHasPiece(pieceLocX, pieceLocY)){
                        //Then check if its not our piece
                        if(!positionHasPieceOfSide(side, pieceLocX, pieceLocY)){
                            //Depending on crowned or not we can jump backwards
                            if(selectedPiece.isCrowned())
                                canJump = true;
                                //We can only jump forward if not crowned.
                                //Since "forward" is subjective to the side.
                            else if(selectedPiece.getSide() == Side.BLACK){
                                if(selectedPiece.posY > y)
                                    canJump = true;
                            }
                            else {
                                if(selectedPiece.posY < y)
                                    canJump = true;
                            }
                        }
                    }
                }
            }
        }

        return canJump;
    }

    private boolean positionHasPieceOfSide(Side side, int x, int y) {
        if(this.board[x][y].getPiece().getSide() == side)
            return true;
        else
            return false;
    }

    private boolean stepsAvailable(Side ourSide) {
        //Loop all the boardpositions
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                Piece piece = board[x][y].getPiece();
                //If there is a piece on the boardposition
                if(piece != null){
                    //Only check when its belonging to the given side
                    if(piece.getSide() == ourSide){
                        //Check if the piece has a possible step
                        if(canStepToPos(piece, piece.posX + 1, piece.posY + 1))
                            return true;
                        else if (canStepToPos(piece, piece.posX + 1, piece.posY - 1))
                            return true;
                        else if (canStepToPos(piece, piece.posX - 1, piece.posY + 1))
                            return true;
                        else if (canStepToPos(piece, piece.posX - 1, piece.posY - 1))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jumpsAvailAble(Side ourSide) {
        //Loop all the boardpositions
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                Piece piece = board[x][y].getPiece();
                //If there is a piece on the boardposition
                if(piece != null){
                    //Only check when its belonging to the given side
                    if(piece.getSide() == ourSide){
                        //Check if the piece has a possible jump
                        if(canJumpToPos(piece, piece.posX + 2, piece.posY + 2))
                            return true;
                        else if (canJumpToPos(piece, piece.posX + 2, piece.posY - 2))
                            return true;
                        else if (canJumpToPos(piece, piece.posX - 2, piece.posY + 2))
                            return true;
                        else if (canJumpToPos(piece, piece.posX - 2, piece.posY - 2))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    void nextTurn(){
        if(this.turn == Side.BLACK){
            this.turn = Side.WHITE;
        } else{
            this.turn = Side.BLACK;
        }
        //Reset the availableMoves to one
        this.availableMoves = 1;
        //Deselect the piece incase it was selected
        this.selectedPiece = null;
        //Decrement the score with 5 every turn. The faster the player won, the highter the score
        this.score = this.score -5;
        //Update the GUI
        updateGUI();
        //Send the game over the network
        sendGame();
    }

    private void sendGame() {
        //Fire the event
        if(listener != null){
            listener.onSendData();
        }
    }

    private void updateGUI() {
        //Fire the event
        if(listener != null){
            listener.onUpdateGUI();
        }
    }

    private void endGame() {
        //Fire the event
        if(listener != null){
            listener.onEndGame();
        }
    }

    private Side getWinningSide(){
        //Returns the winning side if applicable. Returns null if there's not a winner yet.
        boolean whiteWon = true;
        boolean blackWon = true;
        if(stepsAvailable(Side.BLACK) || jumpsAvailAble(Side.BLACK))
            whiteWon = false;
        if(stepsAvailable(Side.WHITE) || jumpsAvailAble(Side.WHITE))
            blackWon = false;
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

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void setOurSide(Side ourSide) {
        this.ourSide = ourSide;
    }

    public Side getOurSide() {
        return ourSide;
    }
}

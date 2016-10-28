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

    void step(){
        //todo implement
        if(this.crowned){

        }else{

        }
    }

    void jump(){
        if(this.crowned){

        }
    }

    boolean canStep(BoardPosition[][] board){
        if(this.crowned){
            //todo implement
            return true;
        }else{
            //todo implement
            return true;
        }
    }

    boolean canJump(BoardPosition[][] board){
        if(this.crowned){
            //If there is a Piece to jump.
            if(true){
                //todo implement
                return true;
            }else{
                return false;
            }
        }else{
            //If there is a Piece to jump.
            if(true){
                //todo implement
                return true;
            }else{
                return false;
            }
        }
    }




}



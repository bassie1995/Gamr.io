package nl.verhoogenvansetten.gamrio.games.checkers.model;

/**
 * Created by Jori on 13-10-2016.
 */

abstract class Piece {

    private boolean crowned = false;
    private Side side = null;

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


    public Piece() {

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



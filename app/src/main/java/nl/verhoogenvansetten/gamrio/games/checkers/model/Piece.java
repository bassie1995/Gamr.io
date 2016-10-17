package nl.verhoogenvansetten.gamrio.games.checkers.model;

/**
 * Created by Jori on 13-10-2016.
 */

abstract class Piece {

    private boolean crowned = false;

    public int getAvailableSteps() {
        return availableSteps;
    }

    public void setAvailableSteps(int availableSteps) {
        this.availableSteps = availableSteps;
    }

    public boolean isCrowned() {
        return crowned;
    }

    public void setCrowned(boolean crowned) {
        this.crowned = crowned;
    }

    private int availableSteps = 1;

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    private Side side = null;

    public Piece() {

    }

    void crown(){
        this.crowned = true;
    }

    void step(){
        this.availableSteps--;
        //todo implement
        if(this.crowned){

        }else{

        }
    }

    void jump(){
        this.availableSteps--;
        if(this.crowned){

        }
    }

    boolean canStep(Piece[][] board){
        if (availableSteps > 0){
            if(this.crowned){
                //todo implement
                return true;
            }else{
                //todo implement
                return true;
            }
        } else {
            return false;
        }
    }

    boolean canJump(Piece[][] board){
        if (availableSteps > 0){
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
        } else {
            return false;
        }
    }




}



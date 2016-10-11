package nl.verhoogenvansetten.gamrio.model;

import java.io.Serializable;

/**
 * Created by Jori on 3-10-2016.
 * Score object model.
 */

public class Score implements Serializable{

    private String playerName;
    private int points;

    public Score() {
    }

    public int getPoints() {
        return points;
    }

    public boolean setPoints(int points) {
        //Only set if the score is 0-MAX_VALUE.
        if (points > -1 && points < Integer.MAX_VALUE){
            this.points = points;
            return true;
        } else{
            //set default value
            this.points = 0;
            return false;
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean setPlayerName(String playerName) {
        //Only set if the playername is longer then 0
        if (playerName.length() > 0) {
            this.playerName = playerName;
            return true;
        } else {
            //set default value
            this.playerName = "ERROR";
            return false;
        }
    }


}

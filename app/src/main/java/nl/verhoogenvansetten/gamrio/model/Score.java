package nl.verhoogenvansetten.gamrio.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Jori on 3-10-2016.
 */

public class Score implements Serializable{

    private String playerName;
    private int points;

    public Score(String playerName, int points) {
        this.playerName = playerName;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


}

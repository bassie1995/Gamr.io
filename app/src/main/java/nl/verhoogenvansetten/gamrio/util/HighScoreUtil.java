package nl.verhoogenvansetten.gamrio.util;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.WriteAbortedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.verhoogenvansetten.gamrio.model.Score;

/**
 * Created by Jori on 3-10-2016.
 */

public class HighScoreUtil {

    public static final String FILENAME_PREFIX = "HS";

    //Public method for getting the highscores for the game specified with the gameID
    // using the default of 10 scores.
    public static List<Score> getHighScoresForGame(int gameId){
        List<Score> highScores = null;
        highScores = getTopScores(gameId, 10);
        return highScores;
    }

    //Public method for getting the highscores for the game specified with the gameID
    // using the given amount of scores to return.
    public static List<Score> getHighScoresForGame(int gameId, int amountOfScores){
        List<Score> highScores = null;
        highScores = getTopScores(gameId, amountOfScores);
        return highScores;
    }

    //Adds a Score
    public static boolean addScore(int gamedId, Score score){
        //Get all the current scores for the relevant gameId. Skips on fresh install.
        List<Score> allScores = new ArrayList<Score>();
        if(InternalStorageUtil.fileExists(FILENAME_PREFIX + gamedId)){
            allScores = getAllScoresForGame(gamedId);
        }
        if(allScores != null){
            //If the list is not null (something would've went wrong)
            allScores.add(score);
            //Save the scores
            try {
                InternalStorageUtil.writeObject(FILENAME_PREFIX + gamedId, allScores);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }

    }


    //Sort the scores and get the X amount of  topScores
    private static List<Score> getTopScores(int gameID, int amountOfScores){
        List<Score> highScores = new ArrayList<Score>();
        List<Score> allScores = getAllScoresForGame(gameID);
        //Only sort when there are scores
        if(allScores != null){
            //Sort the Scores, Scores with higher points first.
            Collections.sort(allScores, new Comparator<Score>() {
                @Override
                public int compare(Score score1, Score score2) {
                    Integer temp = Integer.valueOf(score2.getPoints()).compareTo(score1.getPoints());
                    return temp;
                }
            });
            //Add the scores ascending from the highest to the lowest points to the highscores list.
            //Don't add more than the amountOfScores preferred.
            int i = 0;
            while(i < amountOfScores && i < allScores.size()){
                highScores.add(allScores.get(i));
                i++;
            }
        }
        return highScores;
    }

    //Get all the scores, returns null on exception.
    private static List<Score> getAllScoresForGame(int gameID){
        List<Score> allScores = null;
        try {
             allScores = (List<Score>) InternalStorageUtil.readObject((FILENAME_PREFIX + gameID));
        } catch (ClassNotFoundException e) {
            //TODO implement
            Log.d("","Exception! Wrong object type?");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            //TODO implement
            e.printStackTrace();
        } catch (WriteAbortedException e) {
            //TODO improve
        }catch (IOException e) {
            //TODO implement
            e.printStackTrace();
        }
        return allScores;
    }

}

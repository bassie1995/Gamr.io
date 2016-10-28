package nl.verhoogenvansetten.gamrio.util;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.verhoogenvansetten.gamrio.model.Score;

/**
 * Created by Jori on 3-10-2016.
 * Method for getting/setting high scores of games.
 */

class HighScoreUtil {

    private static final String FILENAME_PREFIX = "HS";

    //Public method for getting the highscores for the game specified with the gameID
    // using the default of 10 scores.
    static List<Score> getHighScoresForGame(int gameId){
        return getTopScores(gameId, 10);
    }

    //Public method for getting the highscores for the game specified with the gameID
    // using the given amount of scores to return.
    static List<Score> getHighScoresForGame(int gameId, int amountOfScores){
        return getTopScores(gameId, amountOfScores);
    }

    //Adds a Score
    static boolean addScore(int gamedId, Score score){
        //Get all the current scores for the relevant gameId. Skips on fresh install.
        List<Score> allScores = new ArrayList<>();
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

    static boolean deleteScoresForGame(int gameID){
        return InternalStorageUtil.deleteFile((FILENAME_PREFIX + gameID));
    }

    //Sort the scores and get the X amount of  topScores
    private static List<Score> getTopScores(int gameID, int amountOfScores){
        List<Score> highScores = new ArrayList<>();
        List<Score> allScores = getAllScoresForGame(gameID);
        //Only sort when there are scores
        if(allScores != null){
            //Sort the Scores, Scores with higher points first.
            Collections.sort(allScores, new Comparator<Score>() {
                @Override
                public int compare(Score score1, Score score2) {
                    return Integer.valueOf(score2.getPoints()).compareTo(score1.getPoints());
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
            Log.e("","Exception! Wrong object type?");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (WriteAbortedException e) {
        }catch (IOException e) {
            e.printStackTrace();
        }
        return allScores;
    }

}

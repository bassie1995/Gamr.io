package nl.verhoogenvansetten.gamrio.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.verhoogenvansetten.gamrio.model.Score;

/**
 * Created by Jori on 3-10-2016.
 */

public class HighScoreUtil {

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
    public static boolean addScore(Score score){
        //todo implement
        return true;
    }

    //Sort the scores and get the X amount of  topScores
    private static List<Score> getTopScores(int gameID, int amountOfScores){
        List<Score> highscores = null;
        List<Score> allScores = getAllScoresForGame(gameID);
        //Sort the Scores, Scores with higher points first.
        Collections.sort(allScores, new Comparator<Score>() {
            @Override
            public int compare(Score score1, Score score2) {
                return score1.getPoints() - score2.getPoints();
            }
        });
        //Add the scores ascending from the highest to the lowest points to the highscores list.
        //Don't add more than the amountOfScores preferred.
        for (int i = 0 ; i < amountOfScores; i++){
            highscores.add(allScores.get(i));
        }
        return highscores;
    }

    //Get all the scores
    private static List<Score> getAllScoresForGame(int gameID){
        List<Score> allScores = null;
        // todo implement
        return allScores;
    }

}

package nl.verhoogenvansetten.gamrio.util;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Random;

import nl.verhoogenvansetten.gamrio.model.Score;

/**
 * Created by Jori on 3-10-2016.
 */

public class HighScoreTest {

    public static void test(Context context){
        Random random = new Random();
        List<Score> scores;
        //Adding scores
        for(int i = 0 ; i<30 ; i++){
            Score score = new Score();
            score.setPlayerName("Player" + (random.nextInt(31) + 1));
            //score.setPoints(random.nextInt(Integer.MAX_VALUE));
            score.setPoints(random.nextInt(1000));
            Log.d("", "Adding score: " + score.getPlayerName() + " : " + score.getPoints());
            HighScoreUtil.addScore(random.nextInt(4)+1, score, context);
        }
        //Getting highscores
        for(int i = 1; i < 5 ; i++){
            scores = HighScoreUtil.getHighScoresForGame(1, 300, context);
            for (Score score: scores){
                Log.d("", "Score: " + score.getPlayerName() + " - " + score.getPoints());
            }
        }
        //Deleting highscores for game
        if(HighScoreUtil.deleteScoresForGame(1, context)){
            Log.d("", "Scores for game 1 deleted");
        }else{
            Log.d("", "Something went wrong deleting scores for game 1");
        }
    }
}

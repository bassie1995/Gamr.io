package nl.verhoogenvansetten.gamrio.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import nl.verhoogenvansetten.gamrio.util.HighScoreUtil;

import java.util.List;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.model.Score;

/**
 * Created by Jori on 29-10-2016.
 */

public class HighScoreActivity extends Activity{

    private int gameID;
    private RecyclerView rv;
    private HighScoreAdapter adapter;
    private int amountOfScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the gameID from the intent
        Intent intent = getIntent();
        gameID = intent.getIntExtra("ID", -1);

        //Get the recyclerview and its linearlayoutmanager
        setContentView(R.layout.highscore_list);
        rv = (RecyclerView)findViewById(R.id.recyclerview_highscore);
        rv.setLayoutManager(new LinearLayoutManager(this));

        //Set the amount of scores
        //Todo make a preference
        amountOfScores = 100;

        //Get the adapter
        adapter = new HighScoreAdapter(HighScoreUtil.getHighScoresForGame(gameID, amountOfScores), this);
        rv.setAdapter(adapter);


    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public class HighScoreAdapter
            extends RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>{

        private List<Score> highScoreList;
        private LayoutInflater inflater;

        public HighScoreAdapter(List<Score> highScoreList, Context context) {
            this.inflater = LayoutInflater.from(context);
            this.highScoreList = highScoreList;
        }

        @Override
        public HighScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = this.inflater.inflate(R.layout.highscore_list_content, parent, false);
            return new HighScoreViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HighScoreViewHolder holder, int position) {
            Score score = this.highScoreList.get(position);
            holder.playerName.setText(score.getPlayerName());
            holder.points.setText(Integer.toString(score.getPoints()));

        }

        @Override
        public int getItemCount() {
            return highScoreList.size();
        }

        class HighScoreViewHolder extends RecyclerView.ViewHolder {

            private TextView playerName;
            private TextView points;
            private View container;

            public HighScoreViewHolder(View itemView) {
                super(itemView);
                playerName = (TextView)itemView.findViewById(R.id.textview_highscore_playername);
                points = (TextView)itemView.findViewById(R.id.textview_highscore_points);
                container = itemView.findViewById(R.id.linearlayout_highscore_content);

            }
        }
    }


}



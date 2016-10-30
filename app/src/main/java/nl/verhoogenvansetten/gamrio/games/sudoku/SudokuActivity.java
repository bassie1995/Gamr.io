package nl.verhoogenvansetten.gamrio.games.sudoku;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.network.Network;

public class SudokuActivity extends GameCompat implements View.OnClickListener{

    Network network;
    DialogFragment newFragment;
    Button clickedButton;
    GridLayout g;
    SharedPreferences prefs;
    int[][] sudoku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ((PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_sudoku_enable", false) &&
                PreferenceManager.getDefaultSharedPreferences(this).getString("pref_sudoku_theme_list", "light").equals("dark")) ||
                PreferenceManager.getDefaultSharedPreferences(this).getString("general_theme_list", "light").equals("dark"))
            setTheme(R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        g = (GridLayout) findViewById(R.id.sudoku_grid);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        network = Network.getInstance();
        network.registerGame(Network.SUDOKU, this);

        network.send(Network.SUDOKU, "CONNECTED");
    }

    private void setupSudoku(int[][] s) {
        sudoku = (s != null ? s : SudokuLogic.removeNumbers(SudokuLogic.generateCompletedSudoku(), 63));

        Button array[][] = new Button[9][9];
        for(int i = 0; i < 81; i++){
            Button newButton;
            if(sudoku[i/9][i%9] != 0){
                newButton = new Button(this, null, R.attr.borderlessButtonStyle);
                newButton.setClickable(false);
            }else{
                newButton = new Button(this);
                newButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_white), PorterDuff.Mode.MULTIPLY);
            }

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.height = 0;
            p.width = 0;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            newButton.setText(sudoku[i/9][i%9] != 0 ? String.valueOf(sudoku[i/9][i%9]) : "");
            newButton.setId(i+1);
            newButton.setLayoutParams(p);
            if(newButton.isClickable()){
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickedButton = (Button) v;
                        newFragment = new SudokuNumberFragment();
                        newFragment.show(getFragmentManager(), "number");
                    }
                });
            }
            array[i/9][i%9] = newButton;
        }

        for(Button ba[] : array){
            for(Button b: ba){
                g.addView(b);
            }
        }
    }

    @Override
    public void update(String data) {
        String[] array = data.split(" ");
        switch (array[0]){
            case "MOVE":
                clickedButton = (Button)findViewById(Integer.parseInt(array[1]));
                changeNumber(Integer.parseInt(array[2]));
                break;
            case "CONNECTED":
                setupSudoku(null);
                sendSudoku();
                break;
            case "SUDOKU":
                setupSudoku(SudokuLogic.stringToSudoku(array[1]));
                break;
            //case "SUDOKU_RECEIVED":

            //    break;
        }
    }

    private void sendSudoku() {
        network.send(Network.SUDOKU, "SUDOKU " + SudokuLogic.sudokuToString(sudoku));
    }



    @Override
    public void onResume() {
        super.onResume();

        String color;
        if (prefs.getBoolean("pref_sudoku_enable", false))
            color = prefs.getString("pref_sudoku_text_color_list", "black");
        else
            color = prefs.getString("general_text_color_list", "black");
        switch (color) {
            case "black":
                setColor(ContextCompat.getColor(this, R.color.md_black));
                break;
            case "yellow":
                setColor(ContextCompat.getColor(this, R.color.md_yellow_500));
                break;
            default:
                setColor(ContextCompat.getColor(this, R.color.md_red_500));
                break;
        }

        if (prefs.getBoolean("pref_sudoku_enable", false))
            setSize(Float.parseFloat(prefs.getString("pref_sudoku_text_size_list", "14")));
        else
            setSize(Float.parseFloat(prefs.getString("general_text_size_list", "14")));
    }

    public void setColor(int color) {
        for (int i = 0; i < g.getChildCount(); i++) {
            Button b = (Button) g.getChildAt(i);
            b.setTextColor(color);
        }
    }

    public void setSize(float size) {
        for (int i = 0; i < g.getChildCount(); i++) {
            Button b = (Button) g.getChildAt(i);
            b.setTextSize(size);
        }
    }

    @Override
    public void peerDown() {

    }

    @Override
    public void peerUp() {

    }

    @Override
    public void onClick(View view) {
        Button b = (Button) view;
        int input = Integer.parseInt((String)b.getText());
        changeNumber(input);
        network.send(Network.SUDOKU, "MOVE " + clickedButton.getId() + " " + input);
        newFragment.getDialog().dismiss();
    }

    private void changeNumber(int i){
        int id = clickedButton.getId()-1;
        sudoku[id/9][id%9] = i;
        clickedButton.setText(String.valueOf(i));
        clickedButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_amber_50), PorterDuff.Mode.MULTIPLY);
    }


}
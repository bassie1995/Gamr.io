package nl.verhoogenvansetten.gamrio.games.bingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.network.Network;


public class BingoGameActivity extends GameCompat {
    GridLayout bingoGrid;
    SharedPreferences prefs;

    Network network;
    int ID = Network.BINGO;

    public Random random = new Random();
    private int num, lineCount;
    private String temp, resultMessage;
    public int[][] elements=new int[5][5];

    AlertDialog gameOverDialog;

    public List<Integer> buttons;
    public final int[] BUTTON_IDS = {
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11, R.id.button12,
            R.id.button13, R.id.button14, R.id.button15, R.id.button16, R.id.button17, R.id.button18,
            R.id.button19, R.id.button20, R.id.button21, R.id.button22, R.id.button23, R.id.button24, R.id.button25
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ((PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_bingo_enable", false) &&
                PreferenceManager.getDefaultSharedPreferences(this).getString("pref_bingo_theme_list", "light").equals("dark")) ||
                PreferenceManager.getDefaultSharedPreferences(this).getString("general_theme_list", "light").equals("dark"))
            setTheme(R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        bingoGrid = (GridLayout) findViewById(R.id.bingo_grid);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Network connection
        network = Network.getInstance();

        define_buttons();

        gameOverDialog = new AlertDialog.Builder(this)
                .setTitle("GAME OVER")
                .setMessage(resultMessage)
                .setCancelable(false)
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finish();
                    }
                })
                .setPositiveButton("Play Again",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent= new Intent(BingoGameActivity.this,BingoGameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create();

    }

    // Make buttons and put random numbers in them
    public void define_buttons() {
        buttons = new ArrayList<>();

        for (int id : BUTTON_IDS) {
            Button button = (Button) findViewById(id);
            num = random.nextInt(39) + 1;

            // Make the numbers don't duplicated
            while (buttons.contains(num)) {
                num = random.nextInt(39) + 1;
            }
            buttons.add(num);
            button.setText(Integer.toString(num));
        }
    }

    // If player click the button,
    public void onClick(View v) {
        Button b = (Button) v;
        temp = b.getText().toString();

        // Mark the position
        findPosition(b);

        // The button's status is changed
        b.setClickable(false);
        b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_red_a200), PorterDuff.Mode.MULTIPLY);
        b.setTextColor(ContextCompat.getColor(this, R.color.md_white));

        // Check the number of lines
        lineCount=checkLine();
        isGameOver(lineCount);
    }

    // Make a virtual table to mark which button is clicked
    public void findPosition(View v) {
        int position= buttons.indexOf(Integer.parseInt(temp));
        elements[position/5][position%5]=1;
    }

    // Check how many lines are exist
    public int checkLine(){
        int lineCount=0;
        for(int i=0; i<5; ++i) {
            int row=0;
            int column=0;
            for(int j=0; j<5; ++j) {
                if(elements[i][j]==1) {
                    ++row;
                }
                if(elements[j][i]==1) {
                    ++column;
                }
            }
            if(row == 5) {
                ++lineCount;
            }
            if(column == 5) {
                ++lineCount;
            }
        }

        int diagonal=0;
        int oppDiagonal=0;
        for(int i=0;i<5;++i){
            if(elements[i][i]== 1){
                ++diagonal;
            }
            if(elements[5-(i+1)][i]==1){
                ++oppDiagonal;
            }
        }
        if(diagonal == 5){
            ++lineCount;
        }
        if(oppDiagonal == 5){
            ++lineCount;
        }
        return lineCount;
    }

    // If the line is over 5, send 'win' message to other player
    public void isGameOver(int lineCount){
        if(lineCount>=5) {
            network.send(ID, "win"+" "+temp);
        }
        else network.send(ID,"number"+" "+temp);
    }

    // If the game is over, show the dialog
    public void showResult(){
        gameOverDialog.setMessage(resultMessage);
        gameOverDialog.show();
    }

    // When other player selected number
    public void updateButtonAndLine(){
        // If the number exist in my bingo table
        if (buttons.contains(Integer.parseInt(temp))) {
            int index = buttons.indexOf(Integer.parseInt(temp));

            // Mark in the virtual table and make the button pressed
            elements[index / 5][index % 5] = 1;
            Button button = (Button) findViewById(getResources().getIdentifier("button" + String.valueOf(index + 1), "id", getPackageName()));
            button.performClick();
            button.setClickable(false);
        }
    }

    // Communication functions

    public void update(String data) {
        String[] array = data.split(" ");
        switch (array[0]){
            case "win":
                temp=array[1];
                updateButtonAndLine();

                if(lineCount >= 5) {
                    resultMessage = "Draw!";
                    network.send(ID,"draw");
                    showResult();
                } else {
                    resultMessage = "You Lose!";
                    network.send(ID,"lose");
                    showResult();
                }
                break;

            case "draw":
                resultMessage = "Draw!";
                showResult();
                break;

            case "lose":
                resultMessage = "You Win!";
                showResult();
                break;

            case "number":
                temp=array[1];
                updateButtonAndLine();
                break;
        }
    }

    @Override
    public void peerUp(){

    }

    @Override
    public void peerDown(){

    }

    @Override
    public void onPause() {
        network.unregisterGame(ID);
        super.onPause();
    }

    @Override
    public void onResume() {
        network.registerGame(ID, this);
        super.onResume();

        String color;
        if (prefs.getBoolean("pref_bingo_enable", false))
            color = prefs.getString("pref_bingo_text_color_list", "black");
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

        if (prefs.getBoolean("pref_bingo_enable", false))
            setSize(Float.parseFloat(prefs.getString("pref_bingo_text_size_list", "14")));
        else
            setSize(Float.parseFloat(prefs.getString("general_text_size_list", "14")));
    }

    public void setColor(int color) {
        for (int i = 0; i < bingoGrid.getChildCount(); i++) {
            Button b = (Button) bingoGrid.getChildAt(i);
            b.setTextColor(color);
        }
    }

    public void setSize(float size) {
        for (int i = 0; i < bingoGrid.getChildCount(); i++) {
            Button b = (Button) bingoGrid.getChildAt(i);
            b.setTextSize(size);
        }
    }
}
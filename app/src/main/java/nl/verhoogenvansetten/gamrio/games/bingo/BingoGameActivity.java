package nl.verhoogenvansetten.gamrio.games.bingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.network.Network;


public class BingoGameActivity extends GameCompat {


    Network network;
    int ID = 5;

    public Random random = new Random();
    private int num, lineCount;
    private String temp, resultMessage;
    private boolean running, win=false;
    public int[][] elements=new int[5][5];


    public List<Integer> buttons;
    public final int[] BUTTON_IDS = {
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11, R.id.button12,
            R.id.button13, R.id.button14, R.id.button15, R.id.button16, R.id.button17, R.id.button18,
            R.id.button19, R.id.button20, R.id.button21, R.id.button22, R.id.button23, R.id.button24, R.id.button25
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);

        // Network connection
        network = Network.getInstance();

        define_buttons();
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

    // If player click the button, it changed and send the number to other player
    public void onClick(View v) {
        Button b = (Button) v;
        b.setClickable(false);
        temp = b.getText().toString();
        findPosition(b);

        b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_red_a200), PorterDuff.Mode.MULTIPLY);
        b.setTextColor(ContextCompat.getColor(this, R.color.md_white));

        lineCount=checkLine();
        isGameOver(lineCount);
    }

    // Make a virtual table to mark which button is clicked
    public void findPosition(View v) {
        int position= buttons.indexOf(Integer.parseInt(temp));
         elements[position/5][position%5]=1;
    }

    // Chenk how many lines are exist
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


    public void isGameOver(int lineCount){
        if(lineCount>=5) {
            network.send(ID, "win"+" "+temp);
        }
        else network.send(ID,"number"+" "+temp);
    }

    // if the game is over, show the dialog
    public void showResult(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BingoGameActivity.this);
        alertDialogBuilder.setTitle("GAME OVER");

        alertDialogBuilder
                .setMessage(resultMessage)
                .setCancelable(false)
                .setNegativeButton("Play Again",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        Intent intent= new Intent(BingoGameActivity.this,BingoGameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setPositiveButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateButtonAndLine(){
        Toast.makeText(this,"Other player selected "+temp,Toast.LENGTH_SHORT).show();

        if (buttons.contains(Integer.parseInt(temp))) {
            int index = buttons.indexOf(Integer.parseInt(temp));

            elements[index / 5][index % 5] = 1;
            Button button = (Button) findViewById(getResources().getIdentifier("button" + String.valueOf(index + 1), "id", getPackageName()));
            button.performClick();
            button.setClickable(false);
        }
    }

    public void update(String data) {
        String[] array = data.split(" ");
        switch (array[0]){
            case "win":
                temp=array[1];
                updateButtonAndLine();

                if(lineCount >= 5) {
                    resultMessage = "Draw!";
                    showResult();
                } else {
                    resultMessage = "You Lose!";
                    network.send(ID,"lose");
                    showResult();
                }
                break;
            case "lose":
                resultMessage = "You Win!";
                break;

            case "number":
                temp=array[1];
                updateButtonAndLine();
                break;
        }
    }

    public void peerUp(){

    }
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
    }
}
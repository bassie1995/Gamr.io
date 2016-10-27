package nl.verhoogenvansetten.gamrio.games.bingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    private int num, lineCount, otherPlayerIineCount;
    private String temp, otherPlayerTemp, resultMessage, buttonsID;
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

        network = Network.getInstance();

        define_buttons();
    }

    public void define_buttons() {
        buttons = new ArrayList<>();

        for (int id : BUTTON_IDS) {
            Button button = (Button) findViewById(id);
            num = random.nextInt(39) + 1;

            while (buttons.contains(num)) {
                num = random.nextInt(39) + 1;
            }
            buttons.add(num);
            button.setText(Integer.toString(num));
        }
    }

    public void onClick(View v) {
        Button b = (Button) v;
        b.setClickable(false);
        findPosition(b);

        b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_red_a200), PorterDuff.Mode.MULTIPLY);
        b.setTextColor(ContextCompat.getColor(this, R.color.md_white));

        temp = b.getText().toString();
        network.send(ID, temp);

        lineCount=checkLine();
        isGameOver(lineCount);
        network.send(ID, lineCount+"");
    }

    public void findPosition(View v) {
        int position= buttons.indexOf(Integer.parseInt(temp));
        elements[position/5][position%5]=1;
    }

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
            if(lineCount==otherPlayerIineCount) {
                resultMessage = "Draw!";
                showResult();
            }
            else{
                resultMessage = "You Win!";
                showResult();
            }
        }else if(otherPlayerIineCount>5){
            resultMessage = "You Lose!";
            showResult();
        }
    }

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

    public void update(String data) {
        // this.otherPlayerLineCount=lineCount;
        // this.otherPlayerTemp=temp;

        if(buttons.contains(otherPlayerTemp)){
            int index = buttons.indexOf(otherPlayerTemp);
            elements[index/5][index%5]=1;
            Button button = (Button) findViewById(getResources().getIdentifier("button"+ String.valueOf(index+1),"id" ,getPackageName()));
            button.performClick();
            button.setClickable(false);
        }
    }

    public void peerUp(){

    }
    public void peerDown(){

    }
}
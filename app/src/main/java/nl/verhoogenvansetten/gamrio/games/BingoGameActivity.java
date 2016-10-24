package nl.verhoogenvansetten.gamrio.games;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public Random random = new Random();
    public int num;
    public String temp;
    public int[][] elements=new int[5][5];
    public String resultMessage;
    int lineCount;

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
        setContentView(R.layout.activity_main);

        define_buttons();
    }

    public void define_buttons() {
        buttons = new ArrayList<Integer>();

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
        b.setBackgroundColor(Color.RED);
        b.setTextColor(Color.WHITE);
        temp = b.getText().toString();
        findPosition(b);

        lineCount=checkLine();
        isGameOver(lineCount);
    }

    public void findPosition(View v) {
        switch (v.getId()) {
            case  R.id.button1: {
                elements[0][0]=1;
                break;
            }
            case  R.id.button2: {
                elements[0][1]=1;
                break;
            }
            case  R.id.button3: {
                elements[0][2]=1;
                break;
            }
            case  R.id.button4: {
                elements[0][3]=1;
                break;
            }
            case  R.id.button5: {
                elements[0][4]=1;
                break;
            }
            case  R.id.button6: {
                elements[1][0]=1;
                break;
            }
            case  R.id.button7: {
                elements[1][1]=1;
                break;
            }
            case  R.id.button8: {
                elements[1][2]=1;
                break;
            }
            case  R.id.button9: {
                elements[1][3]=1;
                break;
            }
            case  R.id.button10: {
                elements[1][4]=1;
                break;
            }
            case  R.id.button11: {
                elements[2][0]=1;
                break;
            }
            case  R.id.button12: {
                elements[2][1]=1;
                break;
            }
            case  R.id.button13: {
                elements[2][2]=1;
                break;
            }
            case  R.id.button14: {
                elements[2][3]=1;
                break;
            }
            case  R.id.button15: {
                elements[2][4]=1;
                break;
            }
            case  R.id.button16: {
                elements[3][0]=1;
                break;
            }
            case  R.id.button17: {
                elements[3][1]=1;
                break;
            }
            case  R.id.button18: {
                elements[3][2]=1;
                break;
            }
            case  R.id.button19: {
                elements[3][3]=1;
                break;
            }
            case  R.id.button20: {
                elements[3][4]=1;
                break;
            }
            case  R.id.button21: {
                elements[4][0]=1;
                break;
            }
            case  R.id.button22: {
                elements[4][1]=1;
                break;
            }
            case  R.id.button23: {
                elements[4][2]=1;
                break;
            }
            case  R.id.button24: {
                elements[4][3]=1;
                break;
            }
            case  R.id.button25: {
                elements[4][4]=1;
                break;
            }
        }
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
            showResult();
        }
    }

    public void showResult(){

        resultMessage="You Win!";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("GAME OVER");

        alertDialogBuilder
                .setMessage(resultMessage)
                .setCancelable(false)
                .setNegativeButton("Play Again",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        Intent intent= new Intent(MainActivity.this,MainActivity.class);
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
}
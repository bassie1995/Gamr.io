package nl.verhoogenvansetten.gamrio.games.sudoku;

import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
    Button button;
    int[][] sudoku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        Button array[][] = new Button[9][9];
        sudoku = SudokuLogic.removeNumbers(SudokuLogic.generateCompletedSudoku(), 63);

        final GridLayout g = (GridLayout) findViewById(R.id.sudoku_grid);
        for(int i = 0; i < 81; i++){
            Button b;
            if(sudoku[i/9][i%9] != 0){
                b = new Button(this, null, R.attr.borderlessButtonStyle);
            }else{
                b = new Button(this);
                //b.setStateListAnimator(null);
                //b.setShadowLayer(0, 0, 0, ContextCompat.getColor(this, R.color.md_white));
                b.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.md_amber_50), PorterDuff.Mode.MULTIPLY);
            }

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.height = 0;
            p.width = 0;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            //b.setText(String.valueOf(i%9+1));
            b.setText(sudoku[i/9][i%9] != 0 ? String.valueOf(sudoku[i/9][i%9]) : "");
            b.setId(i+1);
            b.setLayoutParams(p);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button = (Button) v;

                    newFragment = new SudokuNumberFragment();
                    newFragment.show(getFragmentManager(), "number");

                    //Snackbar.make(g, "Correct: " + SudokuLogic.checkSudoku(sudoku), Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();
                }
            });
            array[i/9][i%9] = b;
        }

        for(Button ba[] : array){
            for(Button b: ba){
                g.addView(b);
            }
        }

        /*
        network = Network.getInstance();
        network.registerGame(1, this);
        */
    }

    public void update(String data) {

    }

    public void peerUp(){}

    public void peerDown(){}

    @Override
    public void peerDown() {

    }

    @Override
    public void peerUp() {

    }

    @Override
    public void onClick(View view) {
        Button b = (Button) view;
        changeNumber(Integer.parseInt((String)b.getText()));
        b.setText("x");
        newFragment.getDialog().dismiss();
    }

    private void changeNumber(int i){
        int id = button.getId()-1;
        sudoku[id/9][id%9] = i;
        button.setText(String.valueOf(i));
    }
}
package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.games.checkers.model.Checkers;
import nl.verhoogenvansetten.gamrio.games.checkers.model.Piece;
import nl.verhoogenvansetten.gamrio.games.checkers.model.Side;

public class CheckersFragment extends Fragment {
    public final static String TAG = "Checkers fragment";
    float scale;
    Checkers checkers = null;
    GridLayout gl = null;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        public void onSendData(Checkers checkers);
        public void onEndGame(Checkers checkers);
    }

    public CheckersFragment() {
    }

    public static CheckersFragment newInstance() {
        CheckersFragment fragment = new CheckersFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            checkers = new Checkers(getActivity());
            //Since its a new game set all the Pieces to the starting positions
            checkers.setUpBoard();
            //Setup the listener for the checkers object
            checkers.setListener(new Checkers.CheckersListener(){
                @Override
                public void onUpdateGUI() {updateGUI();}
                @Override
                public void onSendData() {
                    sendData();
                }
                @Override
                public void onEndGame() {
                    endGame();
                }
            });
        } else {
            //Don't set the starting position
            //Get the saved game by loading the checkers object
            this.checkers = (Checkers)savedInstanceState.getSerializable("checkers");
        }
    }

    private void endGame() {
        //todo end the game
        listener.onEndGame(this.checkers);
    }

    private void sendData() {
        listener.onSendData(this.checkers);
    }

    private void updateGUI() {
        //Add a piece image if the Square contains a piece
        for(int x = 0 ; x < 8; x++){
            for(int y = 0; y < 8; y++){
                Piece piece = checkers.board[x][y].getPiece();
                Square square = checkers.board[x][y].getSquare();
                //If there is a piece on the square draw it
                if(piece != null){
                    if(piece.getSide() == Side.BLACK)
                        if(piece.isCrowned())
                            square.setImageResource(R.drawable.checkers_blackpiece_crowned);
                        else
                            square.setImageResource(R.drawable.checkers_blackpiece);
                    else{
                        if(piece.isCrowned())
                            square.setImageResource(R.drawable.checkers_whitepiece_crowned);
                        else
                            square.setImageResource(R.drawable.checkers_whitepiece);
                    }
                    //Make sure the image fits the button
                    square.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                //if there isnt a piece clear the drawings.
                else{
                    //Set the image to transparent
                    square.setImageResource(android.R.color.transparent);
                }
                //Set the square background color in case it was selected
                square = setDefaultSquareBackgroundColor(square, x, y);
                //Save
                checkers.board[x][y].setSquare(square);
            }
        }
        //Set the background color of the square to red if a square has been selected
        Piece piece = checkers.getSelectedPiece();
        if(piece != null){
            Square selectedSquare = checkers.board[piece.getPosX()][piece.getPosY()].getSquare();
            //Make the background of the square red if the square is selected
            selectedSquare.setBackgroundColor(Color.RED);
            //Save the square
            checkers.board[piece.getPosX()][piece.getPosY()].setSquare(selectedSquare);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GridLayout view = (GridLayout) inflater.inflate(R.layout.fragment_checkers, container, false);
        //Get the GridLayoutView
        gl = (GridLayout) view.getRootView();
        //Calculate the size of the squares
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;
        int padding = (int)((getResources().getDimensionPixelSize(R.dimen.checkers_padding)
                * displaymetrics.density) + 0.5);
        int squareSize;
        if(screenWidth <= screenHeight){
            squareSize = (screenWidth - padding * 2) / gl.getColumnCount();
        }else{
            squareSize = (screenHeight - padding * 2) / gl.getRowCount();
        }

        //Add all the Squares to the GridLayout.
        for(int x = 0 ; x < 8; x++){
            for(int y = 0; y < 8; y++){
                Square square = createNewSquare(x, y);
                //Add the square to the view
                gl.addView(square);
                //After adding the square, set the dimensions
                ((GridLayout.LayoutParams)square.getLayoutParams()).width =  squareSize;
                ((GridLayout.LayoutParams)square.getLayoutParams()).height =  squareSize;
                //Add the Square to the board.
                checkers.board[x][y].setSquare(square);
            }
        }
        //Update the GUI to draw the pieces.
        updateGUI();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save fragmentstate by saving the checkers object
        outState.putSerializable("checkers", this.checkers);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private Square createNewSquare(final int x, final int y){
        Square newSquare = new Square(getActivity(), -1, x, y);
        //Set the color of the button based on the location
        newSquare = setDefaultSquareBackgroundColor(newSquare, x, y);
        //Add the eventlistener for the black squares only. Since those are the only squares
        // which can contain pieces.
        if(newSquare.getColor() == Color.BLACK){
            newSquare.setOnClickListener(new Square.OnClickListener(){
                public void onClick(View v){
                    checkers.turn(x, y);
                }
            });
        }
        //Set the column and row location and weight in the parameters
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.columnSpec = GridLayout.spec(x, 1); //With x as the column and 1 as the weight
        lp.rowSpec = GridLayout.spec(y, 1);
        //Apply the parameters to the Square
        newSquare.setLayoutParams(lp);

        return newSquare;
    }

    private Square setDefaultSquareBackgroundColor(Square square, int x, int y) {
        if((x % 2) == 0)
            if((y % 2) == 0) {
                square.setBackgroundColor(Color.WHITE);
                square.setColor(Color.WHITE);
            }
            else{
                square.setBackgroundColor(Color.BLACK);
                square.setColor(Color.BLACK);
            }
        else
        if((y % 2) == 0) {
            square.setBackgroundColor(Color.BLACK);
            square.setColor(Color.BLACK);
        }
        else {
            square.setBackgroundColor(Color.WHITE);
            square.setColor(Color.WHITE);
        }
        return square;
    }
}

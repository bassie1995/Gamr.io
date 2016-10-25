package nl.verhoogenvansetten.gamrio.games.checkers.ui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.games.checkers.model.Checkers;


public class CheckersFragment extends Fragment {
    public final static String TAG = "Checkers fragment";
    float scale;
    Checkers checkers = null;
    GridLayout gl = null;
    private OnFragmentInteractionListener mListener;

    public CheckersFragment() {
    }

    public static CheckersFragment newInstance() {
        CheckersFragment fragment = new CheckersFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            checkers = new Checkers();
            //Since its a new game set all the Pieces to the starting positions
            checkers.setUpBoard();
        } else {
            //Don't set the starting position
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
            squareSize = (screenWidth - padding) / gl.getColumnCount();
        }else{
            squareSize = (screenHeight - padding) / gl.getRowCount();
        }

        //Add the Squares to the GridLayout.
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


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //todo Save fragment
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private Square createNewSquare(int x, int y){
        Square newSquare = new Square(getActivity());
        //Set the color of the button based on the location
        int remainder = -1;
        if((x % 2) == 0)
            if((y % 2) == 0)
                newSquare.setBackgroundColor(Color.BLACK);
            else
                newSquare.setBackgroundColor(Color.WHITE);
        else
        if((y % 2) == 0)
            newSquare.setBackgroundColor(Color.WHITE);
        else
            newSquare.setBackgroundColor(Color.BLACK);

        //Add the eventlistener
        newSquare.setOnClickListener(new Square.OnClickListener(){
            public void onClick(View v){
                //CharSequence text = "Button Pressed" + square.boardPosX + " " + square.boardPosY;
                CharSequence text = "Button Pressed";

                Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //Set the text of the button
        newSquare.setText("");
        //Set size of buttons
        newSquare.setWidth(0);
        newSquare.setHeight(0);
        //Set the column and row location and weight in the parameters
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.columnSpec = GridLayout.spec(x, 1); //With x as the column and 1 as the weight
        lp.rowSpec = GridLayout.spec(y, 1);
        //Apply the parameters to the Square
        newSquare.setLayoutParams(lp);

        return newSquare;
    }
}

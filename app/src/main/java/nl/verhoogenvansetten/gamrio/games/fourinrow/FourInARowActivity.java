package nl.verhoogenvansetten.gamrio.games.fourinrow;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.GameCompat;
import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.ui.DeviceDialogFragment;
import nl.verhoogenvansetten.gamrio.util.network.Network;

public class FourInARowActivity extends GameCompat implements DeviceDialogFragment.OnFragmentInteractionListener {

    private Network network;
    private int ID = Network.FOURINAROW;
    SharedPreferences prefs;
    private int[][] idGrid = new int[8][8];
    private String[][] valueGrid = new String[8][8];

    private GridLayout fourInRowGrid;
    private AlertDialog startDialog;
    private AlertDialog winDialog;
    private AlertDialog loseDialog;
    private AlertDialog pleaseConnect;
    private AlertDialog restartDialog;
    private Snackbar peerDownSnackbar;

    private boolean hasFirstTurn;
    private boolean isStarted = false;
    private boolean lock;
    private boolean running;
    private boolean gameOver = false;

    private String localPlayer = " ";
    private String otherPlayer = " ";

    private int localScore = 0;
    private int otherScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ((PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_four_in_row_enable", false) &&
                PreferenceManager.getDefaultSharedPreferences(this).getString("pref_four_in_row_theme_list", "light").equals("dark")) ||
                PreferenceManager.getDefaultSharedPreferences(this).getString("general_theme_list", "light").equals("dark"))
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_in_a_row);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Four in a Row");
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        network = Network.getInstance();

        fourInRowGrid = (GridLayout) findViewById(R.id.four_in_row_grid);

        setIdGrid();

        clearBoard();

        lock();

        /** Initiate Dialogs */
        startDialog = new AlertDialog.Builder(this)
                .setTitle("New Game")
                .setMessage("Choose your sign")
                .setCancelable(true)
                .setNegativeButton("X", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        localPlayer = "X";
                        otherPlayer = "O";
                        hasFirstTurn = false;
                        sendStart();
                        startGame();
                        lock();
                    }
                })
                .setPositiveButton("O", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        localPlayer = "O";
                        otherPlayer = "X";
                        hasFirstTurn = false;
                        sendStart();
                        startGame();
                        lock();
                    }
                })
                .create();

        winDialog = new AlertDialog.Builder(this)
                .setTitle("You Win")
                .setMessage("Play again?")
                .setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame();
                    }
                })
                .create();

        loseDialog = new AlertDialog.Builder(this)
                .setTitle("You Lose")
                .setMessage("Play again?")
                .setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame();
                    }
                })
                .create();

        pleaseConnect = new AlertDialog.Builder(this)
                .setTitle("Your are not connected")
                .setMessage("Please connect to a peer")
                .setCancelable(true)
                .setNegativeButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        network.discoverPeers(getSupportFragmentManager());
                    }
                })
                .create();

        restartDialog = new AlertDialog.Builder(this)
                .setTitle("Keep playing or Restart?")
                .setMessage("Restart will reset the score")
                .setCancelable(true)
                .setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startDialog.show();
                    }
                })
                .setPositiveButton("Keep playing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame();
                    }
                })
                .create();

        peerDownSnackbar = Snackbar.make(findViewById(R.id.content_four_in_arow), "Connection to peer is lost. Closing the game will lose your progress", Snackbar.LENGTH_INDEFINITE);
        peerDownSnackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peerDownSnackbar.dismiss();
            }
        });
        peerDownSnackbar.getView().setBackgroundColor(Color.rgb(128, 0, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fourinrow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_play_fourinrow) {
            if (network.isConnected())
                if (gameOver)
                    restartDialog.show();
                else
                    startDialog.show();
            else
                pleaseConnect.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {

        if (lock || !running)
            return;

        Button b = (Button) v;

        String x = String.valueOf(b.getTag().toString().charAt(1));
        String y = String.valueOf(b.getTag().toString().charAt(2));

        if (setCoords(x, y, localPlayer))
            if (localWin(x, y)) {
                gameOver = true;
                lock();
                winDialog.show();
                sendWin(x, y);
                ((TextView) findViewById(R.id.localText)).setTextColor(Color.rgb(0, 128, 0));
                ((TextView) findViewById(R.id.otherText)).setTextColor(Color.rgb(128, 0, 0));
            } else {
                sendCoord(x, y);
            }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Communication functions
     * ---------------------------------------------------------------------------------------------
     */

    public void update(String data) {
        String[] typeData = data.split("\n", 2);
        switch (typeData[0]) {
            case "1":
                recvCoord(typeData[1]);
                break;
            case "2":
                recvWin(typeData[1]);
                break;
            case "3":
                recvGameState(typeData[1]);
                break;
            case "4":
                recvStart(typeData[1]);
                break;
            case "5":
                recvRestart();
                break;
        }
    }

    public void peerDown() {
        peerDownSnackbar.show();
        setToolbarColor(Color.RED);
        running = false;
    }

    public void peerUp() {
        running = true;

        peerDownSnackbar.dismiss();

        if (lock)
            lock();
        else
            unlock();
        if (isStarted) {
            sendGameState();
        }
    }

    /**
     * Communication helper functions.
     */

    private void recvRestart() {
        clearBoard();
        winDialog.cancel();
        loseDialog.cancel();
        startDialog.cancel();
        restartDialog.cancel();
        hasFirstTurn = !hasFirstTurn;
        gameOver = false;
        if (hasFirstTurn)
            unlock();
        else
            lock();
    }

    private void sendRestart() {
        /** 5 to indicate that the game is cleared and restarted*/
        String message = "5\n";
        message += "restart";
        network.send(ID, message);
        unlock();
        gameOver = false;
    }

    private void recvStart(String data) {
        otherPlayer = String.valueOf(data.charAt(0));
        localPlayer = String.valueOf(data.charAt(1));
        loseDialog.cancel();
        winDialog.cancel();
        startDialog.cancel();
        restartDialog.cancel();
        hasFirstTurn = true;
        startGame();
        unlock();
    }

    private void sendStart() {
        /** 4 to indicate that the game has started*/
        String message = "4\n";
        message += localPlayer + otherPlayer;
        network.send(ID, message);
        gameOver = false;
    }

    private void recvGameState(String data) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                valueGrid[i][j] = String.valueOf(data.charAt(i * 8 + j));

        localPlayer = String.valueOf(data.charAt(66));
        otherPlayer = String.valueOf(data.charAt(65));
        isStarted = true;

        /** Is it your turn */

        switch (data.charAt(64)) {
            case 'N':
                lock();
                break;
            case 'Y':
                unlock();
                break;
        }

        /** Was the game over*/
        switch (data.charAt(67)) {
            case 'Y':
                gameOver = true;
                lock();
                break;
            case 'N':
                gameOver = false;
                break;
        }

        String[] scores = data.split("\n", 3);

        /** Get current scores */
        localScore = Integer.valueOf(scores[2]);
        otherScore = Integer.valueOf(scores[1]);

        pushBoard();
        ((TextView) findViewById(R.id.localText)).setText("(" + localPlayer + ")");
        ((TextView) findViewById(R.id.otherText)).setText("(" + otherPlayer + ")");
        ((TextView) findViewById(R.id.localScore)).setText(String.valueOf(localScore));
        ((TextView) findViewById(R.id.otherScore)).setText(String.valueOf(otherScore));

    }

    private void sendGameState() {
        /** 3 to indicate that this is the current gamestate*/
        String message = "3\n";

        /** The first 64 bytes in the message are for the board */
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                message += valueGrid[i][j];

        /** The 65th byte indicated whose turn it is */
        if (lock)
            message += "Y";
        else
            message += "N";

        /** 66 and 67th byte signify the local and other player signs.*/
        message += localPlayer;
        message += otherPlayer;

        /** The 68th byte shows if the game is currently over or not */
        if (gameOver)
            message += "Y";
        else
            message += "N";

        /** The next few bytes will show the score */

        message += "\n" + Integer.toString(localScore);
        message += "\n" + Integer.toString(otherScore);

        network.send(ID, message);
    }

    private void recvCoord(String data) {
        String[] coords = data.split("\n", 3);
        setCoords(coords[0], coords[1], otherPlayer);
        unlock();
    }

    private void sendCoord(String x, String y) {
        /** 1 to indicate that this is a coordinate */
        String message = "1\n";
        message += x + "\n" + y;
        network.send(ID, message);
        lock();
    }

    private void recvWin(String data) {
        String[] coords = data.split("\n", 3);
        setCoords(coords[0], coords[1], otherPlayer);
        sendCoord(coords[0], coords[1]);
        loseDialog.show();
        lock();
        gameOver = true;
        ((TextView) findViewById(R.id.otherText)).setTextColor(Color.rgb(0, 128, 0));
        ((TextView) findViewById(R.id.localText)).setTextColor(Color.rgb(128, 0, 0));
        ((TextView) findViewById(R.id.otherScore)).setText(String.valueOf(++otherScore));
    }

    private void sendWin(String x, String y) {
        /** 2 to indicate that this is a coordinate and the win condition*/
        String message = "2\n";
        message += x + "\n" + y;
        network.send(ID, message);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Helper Functions
     * ---------------------------------------------------------------------------------------------
     */

    private void setToolbarColor(int color) {

        int light;
        int dark;

        switch (color) {
            case Color.RED:
                light = Color.rgb(128, 0, 0);
                dark = Color.rgb(100, 0, 0);
                break;
            case Color.GREEN:
                light = Color.rgb(0, 128, 0);
                dark = Color.rgb(0, 100, 0);
                break;
            case Color.GRAY:
                light = Color.GRAY;
                dark = Color.DKGRAY;
                break;
            default:
                return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = this.getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(dark);
        }


        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(light));

    }

    private void startGame() {
        isStarted = true;
        gameOver = false;
        clearBoard();
        ((TextView) findViewById(R.id.localText)).setText("(" + localPlayer + ")");
        ((TextView) findViewById(R.id.otherText)).setText("(" + otherPlayer + ")");
        ((TextView) findViewById(R.id.localScore)).setText(String.valueOf(localScore = 0));
        ((TextView) findViewById(R.id.otherScore)).setText(String.valueOf(otherScore = 0));

    }

    private void restartGame() {
        sendRestart();
        clearBoard();
        hasFirstTurn = !hasFirstTurn;
        if (hasFirstTurn)
            unlock();
        else
            lock();
    }

    private boolean localWin(String sx, String sy) {
        int x = Integer.valueOf(sx);
        int y = Integer.valueOf(sy);
        int count = 1;
        for (int dx = -1; dx <= 0; dx++)
            for (int dy = -1; dy <= 1 && (dx != 0 || dy != 0); dy++) {
                count += checkRow(x, y, dx, dy);

                count += checkRow(x, y, dx * -1, dy * -1);
                if (count >= 4) {
                    ((TextView) findViewById(R.id.localScore)).setText(String.valueOf(++localScore));
                    return true;
                } else
                    count = 1;
            }
        return false;
    }

    private int checkRow(int x, int y, int dx, int dy) {
        if (x + dx < 8 && x + dx >= 0)
            if (y + dy < 8 && y + dy >= 0)
                if (valueGrid[x + dx][y + dy].equals(localPlayer))
                    return checkRow(x + dx, y + dy, dx, dy) + 1;
        return 0;

    }

    private boolean setCoords(String sx, String sy, String player) {
        int x = Integer.valueOf(sx);
        int y = Integer.valueOf(sy);

        if (!(valueGrid[x][y].equals(" "))) {
            return false;
        }

        ((Button) findViewById(idGrid[x][y])).setText(player);

        valueGrid[x][y] = player;
        return true;
    }

    private void setIdGrid() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                idGrid[i][j] = getResources().getIdentifier("b" + String.valueOf(i) + String.valueOf(j), "id", getPackageName());
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                ((Button) findViewById(idGrid[i][j])).setText(" ");
                valueGrid[i][j] = " ";
            }

    }

    private void pushBoard() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                ((Button) findViewById(idGrid[i][j])).setText(valueGrid[i][j]);
            }
    }

    @Override
    public void onPause() {
        network.unregisterGame(ID);
        super.onPause();
    }

    @Override
    public void onResume() {
        network.registerGame(ID, this);

        if (network.getOtherGameID() == ID)
            running = true;

        super.onResume();

        String color;
        if (prefs.getBoolean("pref_four_in_row_enable", false))
            color = prefs.getString("pref_four_in_row_text_color_list", "black");
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

        if (prefs.getBoolean("pref_four_in_row_enable", false))
            setSize(Float.parseFloat(prefs.getString("pref_four_in_row_text_size_list", "14")));
        else
            setSize(Float.parseFloat(prefs.getString("general_text_size_list", "14")));
    }

    public void setColor(int color) {
        for (int i = 0; i < fourInRowGrid.getChildCount(); i++) {
            Button b = (Button) fourInRowGrid.getChildAt(i);
            b.setTextColor(color);
        }
    }

    public void setSize(float size) {
        for (int i = 0; i < fourInRowGrid.getChildCount(); i++) {
            Button b = (Button) fourInRowGrid.getChildAt(i);
            b.setTextSize(size);
        }
    }

    public void onFragmentInteraction(Uri uri) {
    }

    private void unlock() {
        if (gameOver)
            return;
        this.lock = false;
        setToolbarColor(Color.GREEN);
        ((TextView) findViewById(R.id.localText)).setTextColor(Color.rgb(0, 128, 0));
        ((TextView) findViewById(R.id.otherText)).setTextColor(Color.GRAY);
    }

    private void lock() {
        this.lock = true;
        setToolbarColor(Color.GRAY);
        ((TextView) findViewById(R.id.localText)).setTextColor(Color.GRAY);
        if (isStarted)
            ((TextView) findViewById(R.id.otherText)).setTextColor(Color.rgb(200, 0, 0));
        else
            ((TextView) findViewById(R.id.otherText)).setTextColor(Color.GRAY);
    }
}
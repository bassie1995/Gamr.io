package nl.verhoogenvansetten.gamrio.util;

import nl.verhoogenvansetten.gamrio.GameListActivity;
import nl.verhoogenvansetten.gamrio.R;

/**
 * Created by Daniel on 13-10-2016.
 * Static data per game, with a title and description.
 */

public final class StaticGameData {
    public final static int[] gameIds = {1,2,3,4,5,6,7};
    public final static int[] gameImages = {
            R.drawable.battleship_header,
            R.drawable.battleship_header,
            R.drawable.battleship_header,
            R.drawable.battleship_header,
            R.drawable.battleship_header,
            R.drawable.battleship_header,
            R.drawable.battleship_header
    };
    public final static String[] gameNames = {GameListActivity.getContext().getString(R.string.name_battleship),
            GameListActivity.getContext().getString(R.string.name_bingo),
            GameListActivity.getContext().getString(R.string.name_dots_and_boxes),
            GameListActivity.getContext().getString(R.string.name_sudoku),
            GameListActivity.getContext().getString(R.string.name_tic_tac_toe),
            GameListActivity.getContext().getString(R.string.name_four_in_row),
            GameListActivity.getContext().getString(R.string.name_checkers)
    };
    public final static String[] gameDetails = {
            GameListActivity.getContext().getString(R.string.desc_battleship),
            GameListActivity.getContext().getString(R.string.desc_bingo),
            GameListActivity.getContext().getString(R.string.desc_dots_and_boxes),
            GameListActivity.getContext().getString(R.string.desc_sudoku),
            GameListActivity.getContext().getString(R.string.desc_tic_tac_toe),
            GameListActivity.getContext().getString(R.string.desc_four_in_row),
            GameListActivity.getContext().getString(R.string.desc_checkers)
    };
}

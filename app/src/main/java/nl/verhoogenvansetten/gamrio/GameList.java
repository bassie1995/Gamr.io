package nl.verhoogenvansetten.gamrio;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import nl.verhoogenvansetten.gamrio.model.Game;

class GameList {

    // An array of sample items.
    private static final List<Game> ITEMS = new ArrayList<>();

    // A map of GameItems, by ID.
    private static final SparseArray<Game> ITEM_MAP = new SparseArray<>();

    static {
        // Add some sample items.
        for (int i = 1; i <= 5; i++) {
            addItem(new Game(i, "Item " + i, makeDetails(i)));
        }
    }

    private static void addItem(Game item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    static List<Game> getList(){
        return ITEMS;
    }

    static Game getGame(int id) {
        return ITEM_MAP.get(id);
    }
}

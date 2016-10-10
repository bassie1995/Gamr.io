package nl.verhoogenvansetten.gamrio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.verhoogenvansetten.gamrio.model.Game;

class GameList {

    // An array of sample items.
    private static final List<Game> ITEMS = new ArrayList<>();

    // A map of GameItems, by ID.
    private static final Map<String, Game> ITEM_MAP = new HashMap<>();

    static {
        // Add some sample items.
        for (int i = 1; i <= 5; i++) {
            addItem(new Game(String.valueOf(i), "Item " + i, makeDetails(i)));
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

    static Game getGame(String id) {
        return ITEM_MAP.get(id);
    }
}

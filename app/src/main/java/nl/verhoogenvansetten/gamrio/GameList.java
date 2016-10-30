package nl.verhoogenvansetten.gamrio;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import nl.verhoogenvansetten.gamrio.model.Game;
import nl.verhoogenvansetten.gamrio.util.StaticGameData;

class GameList {

    // An array of sample items.
    private static final List<Game> ITEMS = new ArrayList<>();

    // A map of GameItems, by ID.
    private static final SparseArray<Game> ITEM_MAP = new SparseArray<>();

    static {
        for (int i = 0; i < StaticGameData.gameIds.length; i++) {
            addGame(new Game(StaticGameData.gameIds[i], StaticGameData.gameClasses[i], StaticGameData.gameImages[i], StaticGameData.gameNames[i], StaticGameData.gameDetails[i]));
        }
    }

    private static void addGame(Game item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    static List<Game> getList(){
        return ITEMS;
    }

    static Game getGame(int id) {
        return ITEM_MAP.get(id);
    }

    static List<Game> searchGames(String s){
        List<Game> res = new ArrayList<>();
        for (Game g: ITEMS) {
            if(g.name.contains(s)){
                res.add(g);
            }
        }
        return res;
    }
}

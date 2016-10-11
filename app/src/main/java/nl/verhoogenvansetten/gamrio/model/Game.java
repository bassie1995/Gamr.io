package nl.verhoogenvansetten.gamrio.model;

/**
 * Created by Daniel on 4-10-2016.
 * Game object model.
 */

public class Game {
    public final int id;
    public final String name;
    public final String details;

    public Game(int id, String name, String details) {
        this.id = id;
        this.name = name;
        this.details = details;
    }

    @Override
    public String toString() {
        return name;
    }
}

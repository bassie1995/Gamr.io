package nl.verhoogenvansetten.gamrio.model;

/**
 * Created by Daniel on 4-10-2016.
 * Game object model.
 */

public class Game {
    public final int id;
    public final int image;
    public final String name;
    public final String description;

    public Game(int id, int image, String name, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}

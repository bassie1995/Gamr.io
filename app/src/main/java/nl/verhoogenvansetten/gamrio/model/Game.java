package nl.verhoogenvansetten.gamrio.model;

/**
 * Created by Daniel on 4-10-2016.
 */

public class Game {
    public final String id;
    public final String content;
    public final String details;

    public Game(String id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }
}

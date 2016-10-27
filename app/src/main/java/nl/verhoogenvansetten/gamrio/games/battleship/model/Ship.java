package nl.verhoogenvansetten.gamrio.games.battleship.model;

/**
 * Created by Bas on 25-10-2016.
 * Ship superclass for other ship types to inherit from.
 */

public class Ship {
    private int length;
    private int[] coordinates;
    private int[] buttons;

    public Ship(int length) {
        this.length = length;
        this.coordinates = new int[length];
        this.buttons = new int[length];
    }

    public int getLength() {
        return length;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getButtons() {
        return buttons;
    }

    public void setButtons(int[] buttons) {
        this.buttons = buttons;
    }
}

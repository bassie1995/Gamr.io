package nl.verhoogenvansetten.gamrio.util;

import android.graphics.Color;

/**
 * Created by Bas on 25-10-2016.
 * Simple color util class to know when a color is light or dark
 */

public class ColorUtil {
    public static boolean isColorDark(int color){
        double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        return darkness >= 0.5;
    }
}

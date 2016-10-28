package nl.verhoogenvansetten.gamrio.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Jori on 28-10-2016.
 *
 */

public class MessageUtil {

    public static void showMessage(Context context, String s) {
        if(s != null){
            CharSequence text = (CharSequence)s;
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

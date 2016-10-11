package nl.verhoogenvansetten.gamrio.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Jori on 3-10-2016.
 * Util class for saving/storing data to device storage.
 */

public class InternalStorageUtil {

    // Singleton instance
    private static InternalStorageUtil instance = null;

    // Context property
    // private Context context = null;

    // Constructor because we need Context
    /*private InternalStorageUtil (Context context) {
        this.context = context;
    }*/

    public static InternalStorageUtil getInstance() {// Context context){
        if (instance == null)
            return new InternalStorageUtil();// context);
        else
            return instance;
    }

    //Method for writing objects to the internal storage
    static void writeObject(String fileName, Object object, Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    //Method for reading objects from the internal storage
    static Object readObject(String fileName, Context context) throws IOException, ClassNotFoundException {
        // String dir = context.getFilesDir().getAbsolutePath();
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    static boolean fileExists(String fileName, Context context){
        File file = context.getFileStreamPath(fileName);
        return file != null && file.exists();
    }
}

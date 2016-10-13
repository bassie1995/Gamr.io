package nl.verhoogenvansetten.gamrio.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.verhoogenvansetten.gamrio.GameListActivity;

/**
 * Created by Jori on 3-10-2016.
 * Util class for saving/storing data to device storage.
 */

class InternalStorageUtil {
    //Method for writing objects to the internal storage
    static void writeObject(String fileName, Object object) throws IOException {
        FileOutputStream fos = GameListActivity.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    //Method for reading objects from the internal storage
    static Object readObject(String fileName) throws IOException, ClassNotFoundException {
        // String dir = context.getFilesDir().getAbsolutePath();
        FileInputStream fis = GameListActivity.getContext().openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    static boolean deleteFile(String fileName){
        return (fileExists(fileName) && GameListActivity.getContext().deleteFile(fileName));
    }

    static boolean fileExists(String fileName){
        File file = GameListActivity.getContext().getFileStreamPath(fileName);
        return file != null && file.exists();
    }
}

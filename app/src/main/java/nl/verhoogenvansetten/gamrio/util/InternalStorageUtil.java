package nl.verhoogenvansetten.gamrio.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Jori on 3-10-2016.
 */

public class InternalStorageUtil {

    //Singleton instance
    private static InternalStorageUtil firstInstance = null;

    //Context property
    private static Context context = null;

    //Constructor
    private InternalStorageUtil(Context context){
        this.context = context;
    }

    //Init method for the singleton class InternalStorageUtil
    public static void initInternalStorage(Context context){
        if(firstInstance == null){
           firstInstance = new InternalStorageUtil(context);
        }
    }

    //Method for writing objects to the internal storage
    public static void writeObject(Context context, String fileName, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    //Method for reading objects from the internal storage
    public static Object readObject(Context context, String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }

}

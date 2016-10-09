package nl.verhoogenvansetten.gamrio.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Bas on 29-9-2016.
 * Used as a central hub for permission requests and actions on Android 6.0 and up.
 */

public class PermissionUtil {
    private final int PERMISSION_CONSTANT = 1; // Make one per permission

    public void checkPermission(Activity activity, String permission, int permissionConstant) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, new String[]{permission},
                        permissionConstant);
                // permissionConstant is an app-defined int constant. The callback
                // method gets the result of the request.
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CONSTANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted! Do the task.
                } else {
                    // Permission denied. Disable the functionality we asked permissions for.
                }
                break;
            }
            // Other cases for other permissions.
        }
    }
}

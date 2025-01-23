package bluen.homein.gayo_security.helper;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

public class PermissionsHelper {

    public static void checkPermissions(Context context,
                                        PermissionListener listener,
                                        String deniedMessage,
                                        String... permissions) {

        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage(deniedMessage)
                .setPermissions(permissions)
                .check();
    }

    public static boolean isPermissionGranted(Context context,
                                              String... permissions) {
        boolean isPermissionCheck = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionCheck = false;

                break;
            }
        }

        return isPermissionCheck;
    }
}

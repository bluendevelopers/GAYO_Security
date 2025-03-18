package bluen.homein.gayo_security.permission;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;


public class PermissionInfo {

    public static final String[] CAMERA_PERMISSION = new String[]{
            Manifest.permission.CAMERA
    };

    public static final String[] RECORD_PERMISSION = new String[]{
            Manifest.permission.RECORD_AUDIO
    };
}

package bluen.homein.gayo_security.preference;

import android.Manifest;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.util.UUID;

public class Gayo_Preferences {

    public static final String GLOBAL_INFO = "global_info_pref_key";

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "";
    public static final String ROOTING_PATH_1 = "/system/bin/su";
    public static final String ROOTING_PATH_2 = "/system/xbin/su";
    public static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    public static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";

    public static final String[] RootFilesPath = new String[]{ROOT_PATH + ROOTING_PATH_1,
            ROOT_PATH + ROOTING_PATH_2,
            ROOT_PATH + ROOTING_PATH_3,
            ROOT_PATH + ROOTING_PATH_4
    };
}

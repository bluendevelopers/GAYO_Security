package bluen.homein.gayo_security.global;

import static bluen.homein.gayo_security.activity.call.CallActivity.CALL_STATUS_IDLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDexApplication;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class GlobalApplication extends MultiDexApplication {
    private static final String TAG = "GlobalApplication";
    public static int callStatus = CALL_STATUS_IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
        String macAddress = getMacAddress(getApplicationContext());
        Log.d("MAC", "Device MAC: " + macAddress);
    }

    public String getMacAddress(Context context) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : interfaces) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00"; // 기본값 (제한되었을 경우)
    }
}

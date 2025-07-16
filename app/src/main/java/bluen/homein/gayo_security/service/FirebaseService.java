package bluen.homein.gayo_security.service;

import static org.webrtc.ContextUtils.getApplicationContext;

import static bluen.homein.gayo_security.activity.call.CallActivity.CALL_STATUS_IDLE;
import static bluen.homein.gayo_security.global.GlobalApplication.callStatus;
import static bluen.homein.gayo_security.preference.Gayo_Preferences.DEVICE_NOT_CONNECTED;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import bluen.homein.gayo_security.activity.call.CallActivity;
import bluen.homein.gayo_security.global.GlobalApplication;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;

public class FirebaseService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseService";

    private Gayo_SharedPreferences mPrefGlobal = null;
    private NotificationCompat.Builder builder = null;
    private TextToSpeech textToSpeech;
    private String ttsMsg = "";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (null == mPrefGlobal) {
            mPrefGlobal = new Gayo_SharedPreferences(getApplicationContext(), Gayo_Preferences.GLOBAL_INFO);
        }
        mPrefGlobal.setFirebaseToken(token);

        Log.i(TAG, "onNewToken:" + token);
    }

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (null == mPrefGlobal) {
            mPrefGlobal = new Gayo_SharedPreferences(getApplicationContext(), Gayo_Preferences.GLOBAL_INFO);
        }

//        sendIntent(remoteMessage, remoteMessage.getData().get("dialog_title"), remoteMessage.getData().get("msg"));
        try {
            handleServerMessage(remoteMessage);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    private void sendIntent(RemoteMessage remoteMessage, String title, String msg) {
//        GlobalApplication application = (GlobalApplication) getApplicationContext();
//        Intent intent = null;
//        Log.e(TAG, "Type: " + remoteMessage.getData().get("type"));
//        Log.e(TAG, "Push Type: " + remoteMessage.getData().get("pushType"));
//        Log.e(TAG, "Action: " + remoteMessage.getData().get("action"));
//
//        try {
//            // Push Action Check
//            switch (remoteMessage.getData().get("action")) {
//
//            }
//        } catch (NullPointerException e) {
//            Log.e(TAG, "Remote Data Null");
//            e.printStackTrace();
//        }
//
//    }

    private void handleServerMessage(RemoteMessage remoteMessage) throws JSONException {
        // 1) JSON 파싱
        //    실제로는 JSONObject 를 이용해서 "method" 필드를 확인
        // 1) JSON 파싱 (RemoteMessage -> JSONObject)
        Map<String, String> data = remoteMessage.getData();

        // 또는, 방법 B: 특정 키에 JSON 문자열만 담겨 오는 경우
        JSONObject json = new JSONObject(data);
        // 이제 json.toString()을 Gson으로 다시 파싱
        WebSocketService.WebSocketBody body =
                new Gson().fromJson(json.toString(), WebSocketService.WebSocketBody.class);

        String message = body.getMessage() != null ? body.getMessage() : "";

        if (callStatus == CALL_STATUS_IDLE && body.getMethod().equals("invite") || callStatus == CALL_STATUS_IDLE && body.getMethod().equals("OK")
                || callStatus != CALL_STATUS_IDLE || message.equals(DEVICE_NOT_CONNECTED)) {
            if (isAppInForeground(getApplicationContext())) {
                // 앱이 현재 실행중(포그라운드)이면 곧바로 CallActivity로 이동
                if (mPrefGlobal.getAuthorization() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem != null) {
                    Intent serviceIntent = new Intent(this, WebSocketService.class);
                    serviceIntent.putExtra("deviceBody", Gayo_SharedPreferences.PrefDeviceData.prefItem);
                    serviceIntent.putExtra("authorization", mPrefGlobal.getAuthorization());
                    serviceIntent.putExtra("websocketMessage", json.toString());
                    startService(serviceIntent);
                }
            }
        }

    }

    public boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        if (processes == null) return false;

        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && processInfo.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}

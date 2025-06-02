package bluen.homein.gayo_security.service;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

    }
}

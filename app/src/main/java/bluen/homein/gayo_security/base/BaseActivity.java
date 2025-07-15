package bluen.homein.gayo_security.base;

import static bluen.homein.gayo_security.global.GlobalApplication.callStatus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bluen.homein.gayo_security.activity.MainActivity;
import bluen.homein.gayo_security.dialog.PopupDialog;
import bluen.homein.gayo_security.dialog.ProgressDialog;
import bluen.homein.gayo_security.global.GlobalApplication;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected static Context mContext = null;
    public final String API_RESULT_OK = "O";
    public ProgressDialog progressDialog;
    public Gayo_SharedPreferences mPrefGlobal = null;
    public boolean mIsFinish;
    public boolean mIsError;
    public String TAG = "";
    public Vibrator vibrator;
    public PopupDialog popupDialog;


    //  public String serialCode = "testserialCode1"; //전화 거는 기기
//    public String serialCode = "testserialCode6"; //전화 받는 기기

    private Handler sleepHandler = new Handler(Looper.getMainLooper());
    private Handler goToMainHandler = new Handler(Looper.getMainLooper());
    private Runnable sleepRunnable;
    private Runnable goToMainRunnable;
    private int sleepTimeoutSeconds = 0;
    private int goToMainSeconds = 0;
    private float originalBrightness;

    protected abstract int getLayoutResId();

    protected abstract void initActivity(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        popupDialog = new PopupDialog(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (null == mContext) {
            mContext = getApplicationContext();
        }

        setContentView(getLayoutResId());

        ButterKnife.bind(this);

        if (null == mPrefGlobal) {
            mPrefGlobal = new Gayo_SharedPreferences(getApplicationContext(), Gayo_Preferences.GLOBAL_INFO);
        }

        initActivity(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Gayo_SharedPreferences.PrefDeviceData.prefItem != null) {
            if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody() != null) {
                applySavedBrightness();
            }
            if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody() != null) {
                if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody().getSleepTime() != 0) {
                    sleepTimeoutSeconds = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody().getSleepTime();
                    startSleepTimer();
                } else {
                    if (sleepRunnable != null) {
                        stopSleepTimer();
                    }
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody().getMainReturnTime() != 0) {
                    goToMainSeconds = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody().getMainReturnTime();
                    startGoToMainTimer();
                } else {
                    if (goToMainRunnable != null) {
                        stopGoToMainTimer();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Gayo_SharedPreferences.PrefDeviceData.prefItem != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody() != null) {
            stopSleepTimer();
            stopGoToMainTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgress();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (Gayo_SharedPreferences.PrefDeviceData.prefItem != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody() != null) {
            if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody().getSleepTime() != 0) {
                resetSleepTimer();
            }
            if (Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody().getMainReturnTime() != 0) {
                resetGoToMainTimer();
            }
        }
    }

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void startActivity(Class<?> activity, String tag, int class_state) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(tag, class_state);
        startActivity(intent);
    }

    public void startActivityForResult(Class<?> activity, int requestCode) {
        Intent intent = new Intent(this, activity);
        startActivityForResult(intent, requestCode);
    }

    public void addFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerId, fragment)
                .commit();
    }

    public void replaceFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void showToastShort(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
    }

    public void showToastShort(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

//    public void initAdView() {
//        AdView adView = findViewById(R.id.adView);
//        if (null == adView) {
//            return;
//        }
//        LinearLayout layMainBanner = findViewById(R.id.lay_bottom_banner_area);
//        ImageView imgBottomBanner = findViewById(R.id.img_bottom_banner);
//        postBottomBanner(imgBottomBanner);
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                mIsAdviewSet = true;
//                adView.setVisibility(View.VISIBLE);
//                layMainBanner.setVisibility(View.GONE);
//            }
//        });
//    }
//
//    public void visibleAdView() {
//        AdView adView = findViewById(R.id.adView);
//        LinearLayout layMainBanner = findViewById(R.id.lay_bottom_banner_area);
//        if (mIsAdviewSet) {
//            adView.setVisibility(View.VISIBLE);
//        } else {
//            layMainBanner.setVisibility(View.VISIBLE);
//        }
//    }
//
//    public void invisibleAdView() {
//        AdView adView = findViewById(R.id.adView);
//        LinearLayout layMainBanner = findViewById(R.id.lay_bottom_banner_area);
//        if (adView.getVisibility() == View.GONE) {
//            layMainBanner.setVisibility(View.GONE);
//        } else {
//            adView.setVisibility(View.GONE);
//        }
//    }


    public void showProgress() {
        if (null == progressDialog && !this.isFinishing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
        }
    }

    public void closeProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showPopupDialog(String _title, String _sub, String _cancel, String _confirm) {
        if (null != popupDialog) {
            popupDialog.showAlertDialog(_title, _sub, _cancel, _confirm);
        }
    }

    public void showPopupDialog(String _title, String _sub, String _confirm) {
        if (null != popupDialog) {
            popupDialog.showAlertDialog(_title, _sub, _confirm);
        }
    }

    public void showPopupDialog(String _msg, String _confirm) {
        if (null != popupDialog) {
            popupDialog.showAlertDialog(_msg, _confirm);
        }
    }

    public void showWarningDialog(String _msg, String _confirm) {
        if (null != popupDialog) {
            popupDialog.showWarningDialog(_msg, _confirm);
        }
    }

    public void showWarningDialog(String _msg, String _cancel, String _confirm) {
        if (null != popupDialog) {
            popupDialog.showWarningDialog(_msg, _cancel, _confirm);
        }
    }

    public void showPopupDialog(String _additionalMsg, String _title, String _sub, String _cancel, String _confirm) {
        if (null != popupDialog) {
            popupDialog.showAlertDialog(_additionalMsg, _title, _sub, _cancel, _confirm);
        }
    }

//    public void hideNavigationBar() {
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // Immersive 모드 추가
//        decorView.setSystemUiVisibility(uiOptions);
//    }

    public void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideNavigationBar();
                }
            }
        });
    }

    private void applySavedBrightness() {
        float brightness = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody().getBrightness();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 100f;
        getWindow().setAttributes(layoutParams);
    }

    public class BaseWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            closeProgress();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgress();
        }
    }

    public static class ErrorBodyParser {

        public static final int ERROR_CODE_NUM = 0;
        public static final int ERROR_MESSAGE_NUM = 1;

        public static String[] JsonParser(String data) {

            String[] result = new String[10];

            if (data.contains("result")) {
                try {
                    JsonParser jsonParser = new JsonParser();
                    //json 데이터가 이미 "[{a,b,c},{d,e,f}]"와 같이 String으로 가지고 있거나,
                    //json 파일에서 Reader를 통해 텍스트를 읽어 들이는 방법입니다.

                    JsonObject jsonObject = (JsonObject) jsonParser.parse(data);

                    result[ERROR_CODE_NUM] = (jsonObject.get("result")).toString();
                    result[ERROR_MESSAGE_NUM] = (jsonObject.get("message")).toString();

                } catch (NullPointerException e) {
                    result = null;
                    e.printStackTrace();
                }
            }

            return result;
        }
    }

    public void setSleepTimeoutSeconds(int seconds) {
        sleepTimeoutSeconds = seconds;
        if (seconds != 0) {
            resetSleepTimer();
        } else {
            stopSleepTimer();
        }
    }

    public void setGoToMainSeconds(int seconds) {
        goToMainSeconds = seconds;
        if (seconds != 0) {
            resetGoToMainTimer();
        } else {
            stopGoToMainTimer();
        }
    }

//    private void startSleepTimer() {
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        sleepRunnable = new Runnable() {
//            @Override
//            public void run() {
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            }
//        };
//        sleepHandler.postDelayed(sleepRunnable, sleepTimeoutSeconds * 1000);
//
//    }
//
//    private void resetSleepTimer() {
//        sleepHandler.removeCallbacks(sleepRunnable);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        sleepHandler.postDelayed(sleepRunnable, sleepTimeoutSeconds * 1000);
//    }
//
//    private void stopSleepTimer() {
//        sleepHandler.removeCallbacks(sleepRunnable);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    }

    private void startSleepTimer() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sleepRunnable = new Runnable() {
            @Override
            public void run() {
                dimAndTurnOffScreen();
            }
        };

        sleepHandler.postDelayed(sleepRunnable, sleepTimeoutSeconds * 1000);
    }

    private void resetSleepTimer() {
        sleepHandler.removeCallbacks(sleepRunnable);
        restoreBrightness();
        startSleepTimer();
    }

    private void stopSleepTimer() {
        sleepHandler.removeCallbacks(sleepRunnable);
        restoreBrightness();
    }

    private void dimAndTurnOffScreen() {
        // 서서히 어두워지는 효과 구현
        ValueAnimator animator = ValueAnimator.ofFloat((float) Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody().getBrightness() / 100, 0f);
        animator.setDuration(2000); // 5초간 서서히 어두워짐
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float brightness = (float) animation.getAnimatedValue();
                setScreenBrightness(brightness);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 화면이 완전히 어두워진 후 KEEP_SCREEN_ON 해제
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        animator.start();
    }

    private void restoreBrightness() {
        setScreenBrightness((float) Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody().getBrightness() / 100);
    }

    private void setScreenBrightness(float brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness;
        getWindow().setAttributes(layoutParams);
    }

    private void startGoToMainTimer() {

        goToMainRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        };
        if (callStatus < 2) {
            goToMainHandler.postDelayed(goToMainRunnable, goToMainSeconds * 1000);
        } else {
            stopGoToMainTimer();
        }

    }

    private void resetGoToMainTimer() {
        goToMainHandler.removeCallbacks(goToMainRunnable);
        startGoToMainTimer();
    }

    private void stopGoToMainTimer() {
        goToMainHandler.removeCallbacks(goToMainRunnable);
    }
}

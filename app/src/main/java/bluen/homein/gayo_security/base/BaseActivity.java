package bluen.homein.gayo_security.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bluen.homein.gayo_security.dialog.PopupDialog;
import bluen.homein.gayo_security.dialog.ProgressDialog;
import bluen.homein.gayo_security.global.GlobalApplication;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected static Context mContext = null;
    public final String API_RESULT_OK = "O";
    public GlobalApplication application;
    public ProgressDialog progressDialog;
    public Gayo_SharedPreferences mPrefGlobal = null;
    public boolean mIsFinish;
    public String TAG = "";
    public Vibrator vibrator;
    public PopupDialog popupDialog;
    public String buildingCode = "0004";

//        public String serialCode = "testserialCode1"; //전화거는 기기
    public String serialCode = "testserialCode6"; //전화받는 기기
//    되는 기기
    public String macAddress = "00-00-00-00"; //더미
    public String ipAddress = "192.168.0.102"; //더미

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

        application = (GlobalApplication) getApplicationContext();

        if (null == mPrefGlobal) {
            mPrefGlobal = new Gayo_SharedPreferences(getApplicationContext(), Gayo_Preferences.GLOBAL_INFO);
        }

        initActivity(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeProgress();
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

        // UI 가시성 변경 감지 리스너 추가
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // 네비게이션 바가 다시 나타난 경우 다시 숨김 처리
                    hideNavigationBar();
                }
            }
        });
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
}

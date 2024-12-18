package bluen.homein.gayo_security.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


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
    public boolean mIsAdviewSet;
    public String TAG = "";

    protected abstract int getLayoutResId();

    protected abstract void initActivity(Bundle savedInstanceState);

    private String pushCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.e("BaseActivity", "Android Version 8 Error");
        }

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

}

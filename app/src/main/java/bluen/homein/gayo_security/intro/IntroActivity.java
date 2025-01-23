package bluen.homein.gayo_security.intro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


import bluen.homein.gayo_security.MainActivity;
import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends BaseActivity {

    private Intent intent;
    private static final int REQUEST_CODE_PERMISSION_CLEAR = 10;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        TAG = getLocalClassName();
//        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
//            @Override
//            public void onSysCheck() {
//
//            }
//
//            @Override
//            public void onFinish() {
//                if (mIsFinish) {
//                    finish();
//                }
//            }
//
//            @Override
//            public void onNextStep() {
//                if (mIsFinish) {
//                    mIsFinish = false;
//                }
//                rootingCheck();
//            }
//
//            @Override
//            public void onReturn(boolean _flag) {
//
//            }
//        });

        rootingCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PERMISSION_CLEAR) {
            if (permissionCheck()) {
                doLogin();
//                goToMain();
            } else {
                mIsFinish = true;
//                showPopupDialog(getString(R.string.permission_denied_2), getString(R.string.close), getString(R.string.confirm));
            }
        }
    }

    private void rootingCheck() {
        if (getRootingDevice()) {
            mIsFinish = true;
//                showPopupDialog(getString(R.string.rooting_error), false);
        } else {
            if (!permissionCheck()) {
//                        Intent intent = new Intent(IntroActivity.this, IntroPermissionGuideActivity.class);
//                        intent.putExtra("gps_permission", PermissionInfo.GPS_PERMISSION);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            intent.putExtra("gps_bg_permission", PermissionInfo.GPS_BACKGROUND_PERMISSION);
//                        }
//                        startActivityForResult(intent, REQUEST_CODE_PERMISSION_CLEAR);
            } else {
                doLogin();

            }
        }
    }

    private static boolean getRootingDevice() {
        boolean isRootingFlag = false;

        try {
            Process process = Runtime.getRuntime().exec("find / -name su");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (reader.ready() == false) {
                return false;
            }

            String result = reader.readLine();
            if (result.contains("/su") == true) {
                isRootingFlag = true;
            }

        } catch (Exception e) {
            isRootingFlag = false;
        }

        if (!isRootingFlag) {
            isRootingFlag = checkRootingFiles(Gayo_Preferences.RootFilesPath);
        }

        return isRootingFlag;
    }

    private static boolean checkRootingFiles(String[] filePaths) {
        boolean result = false;
        File file;

        for (String path : filePaths) {
            file = new File(path);

            if (file != null && file.exists() && file.isFile()) {
                result = true;
                break;
            } else {
                result = false;
            }
        }

        return result;
    }

    private boolean permissionCheck() {
        // 필수 권한 체크 - 카메라

        return true;
    }

    private void doLogin() {
        showProgress();

        Retrofit.AuthApi loginInterface = Retrofit.AuthApi.retrofit.create(Retrofit.AuthApi.class);

        Call<ResponseDataFormat.LoginData> call = loginInterface.getAuthInfo(new RequestDataFormat.DeviceInfoBody(serialCode, buildingCode,macAddress,ipAddress)); // test
        call.enqueue(new Callback<ResponseDataFormat.LoginData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.LoginData> call, Response<ResponseDataFormat.LoginData> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getAuthorization() != null) {
                        mPrefGlobal.setAuthorization(response.body().getAuthorization());
                        goToMain();
                    }else {

                    }
                }else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.LoginData> call, Throwable t) {
                closeProgress();

            }
        });

    }

    private void goToMain() {
        Log.e(TAG, "goToMain");

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

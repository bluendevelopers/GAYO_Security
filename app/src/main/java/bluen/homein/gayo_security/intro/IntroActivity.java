package bluen.homein.gayo_security.intro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


import bluen.homein.gayo_security.activity.MainActivity;
import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.helper.PermissionsHelper;
import bluen.homein.gayo_security.permission.PermissionInfo;
import bluen.homein.gayo_security.preference.Gayo_Preferences;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends BaseActivity {

    private Intent intent;
    private static final int REQUEST_CODE_PERMISSION_CLEAR = 10;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 4;
    private static final int PERMISSION_REQUEST_CODE_AUDIO_RECORD = 5;
    private boolean mPermissionIsDenied;

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

    private void rootingCheck() {
        if (getRootingDevice()) {
            mIsFinish = true;
//                showPopupDialog(getString(R.string.rooting_error), false);
        } else {
            if (!PermissionsHelper.isPermissionGranted(this, PermissionInfo.CAMERA_PERMISSION)) {
                requestPermissions(PermissionInfo.CAMERA_PERMISSION, PERMISSION_REQUEST_CODE_CAMERA);
            } else if (!PermissionsHelper.isPermissionGranted(this, PermissionInfo.RECORD_PERMISSION)) {
                requestPermissions(PermissionInfo.RECORD_PERMISSION, PERMISSION_REQUEST_CODE_AUDIO_RECORD);

            } else {
                Gayo_SharedPreferences.PrefDeviceData.initPrefDeviceData(mContext);
                goToMain();
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

    private void getDeviceData() {
        Retrofit.PreferencesApi loginApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<RequestDataFormat.DeviceBody> call = loginApi.loadDeviceDataPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(serialCode, buildingCode)); // test

        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult() == null) {
                        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(mContext, response.body());
                        goToMain();

                    } else {

                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                closeProgress();

            }
        });
    }


    private void doLogin() {
        showProgress();

        Retrofit.LoginApi loginInterface = Retrofit.LoginApi.retrofit.create(Retrofit.LoginApi.class);

        Call<ResponseDataFormat.LoginData> call = loginInterface.getAuthInfo(new RequestDataFormat.DeviceInfoBody(serialCode, buildingCode, macAddress, ipAddress)); // test
        call.enqueue(new Callback<ResponseDataFormat.LoginData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.LoginData> call, Response<ResponseDataFormat.LoginData> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getAuthorization() != null) {
                        mPrefGlobal.setAuthorization(response.body().getAuthorization());
//                        goToMain();
                        Gayo_SharedPreferences.PrefDeviceData.initPrefDeviceData(mContext);
                        goToMain();
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.LoginData> call, Throwable t) {
                closeProgress();

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
            boolean isGrantResult = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isGrantResult = false;
                    break;
                }
            }
            if (isGrantResult) {
                if (!PermissionsHelper.isPermissionGranted(this, PermissionInfo.CAMERA_PERMISSION)) {
                    requestPermissions(PermissionInfo.CAMERA_PERMISSION, PERMISSION_REQUEST_CODE_CAMERA);
                } else {
                    rootingCheck();
                }
            } else {
                boolean isCameraPermissionDenied = false;
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        isCameraPermissionDenied = true;
                        break;
                    }
                }
                continuouslyDenied(isCameraPermissionDenied, permissions);
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE_AUDIO_RECORD) {
            boolean isGrantResult = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isGrantResult = false;
                    break;
                }
            }
            if (isGrantResult) {
                if (!PermissionsHelper.isPermissionGranted(this, PermissionInfo.CAMERA_PERMISSION)) {
                    requestPermissions(PermissionInfo.RECORD_PERMISSION, PERMISSION_REQUEST_CODE_AUDIO_RECORD);
                } else {
                    rootingCheck();
                }
            } else {
                boolean isCameraPermissionDenied = false;
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        isCameraPermissionDenied = true;
                        break;
                    }
                }
                continuouslyDenied(isCameraPermissionDenied, permissions);
            }
        }

    }

    private void continuouslyDenied(boolean isDenied, String[] permissions) {
        if (!isDenied) {
            mIsFinish = false;
            mPermissionIsDenied = true;
            showPopupDialog("권한 페이지로 이동하여\n카메라 권한을 허용해 주세요.", "확 인");
        } else {
            mPermissionIsDenied = false;
            if (!PermissionsHelper.isPermissionGranted(this, permissions)) {
                mIsFinish = true;
                showPopupDialog("카메라 권한을 허용해주세요", "확 인");
            }
        }
    }

    private void goToMain() {
        Log.e(TAG, "goToMain");

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

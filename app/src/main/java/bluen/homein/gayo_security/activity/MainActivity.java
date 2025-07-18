package bluen.homein.gayo_security.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.call.CallActivity;
import bluen.homein.gayo_security.activity.callRecord.CallRecordActivity;
import bluen.homein.gayo_security.activity.changeWorker.ChangeWorkerActivity;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.activity.visitHistory.VisitHistoryListActivity;
import bluen.homein.gayo_security.activity.workRecord.WorkRecordActivity;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.databinding.ActivityMainBinding;
import bluen.homein.gayo_security.dialog.PopupDialog;
import bluen.homein.gayo_security.global.GlobalApplication;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import bluen.homein.gayo_security.service.WebSocketService;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_MAIN = 1;
    public static final int RESULT_CHANGED_WORKER = 2;
    private static final int REQUEST_CODE_FIRST_SETTING = 3;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @BindView(R.id.textClock)
    TextClock textClock;
    @BindView(R.id.tv_security_state_1)
    TextView tvSecurityState1;
    @BindView(R.id.tv_worker_phone_number)
    TextView tvWorkerPhoneNumber;
    @BindView(R.id.tv_worker_name)
    TextView tvWorkerName;
    @BindView(R.id.iv_patrol_btn)
    ImageView ivPatrolBtn;
    @BindView(R.id.tv_security_state_2)
    TextView tvSecurityState2;
    @BindView(R.id.tv_year_and_month)
    TextView tvYearAndMonth;
    @BindView(R.id.tv_day_number)
    TextView tvDayNumber;
    @BindView(R.id.tv_week_day)
    TextView tvWeekDay;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_region_info)
    TextView tvRegionInfo;
    @BindView(R.id.iv_weather_img)
    ImageView ivWeatherImg;

    private final BroadcastReceiver dateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateDateTexts();
            getWeatherInfo();
        }
    };

    @OnClick(R.id.iv_patrol_btn)
    void clickSecurityOnOffBtn() {

        boolean patrolMode = mPrefGlobal.getPatrolMode();
        patrolMode = !patrolMode;

        if (patrolMode) {
            ivPatrolBtn.setImageResource(R.drawable.on_icon);
            tvSecurityState1.setText("경비실기 - 순찰 중");
            tvSecurityState2.setText("순찰 ON");
        } else {
            ivPatrolBtn.setImageResource(R.drawable.off_icon);
            tvSecurityState1.setText("경비실기 - 근무 중");
            tvSecurityState2.setText("순찰 OFF");
        }
        mPrefGlobal.setPatrolMode(patrolMode);
        updatePatrolMode();
    }

    @OnClick(R.id.lay_back_btn)
    void clickBackBtn() {
        onBackPressed();
    }

    @OnClick(R.id.lay_worker_info)
    void clickLayWorkerInfo() {
        clickChangeWorkerBtn();
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.lay_security_on_off_btn)
    void clickWorkToggleBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        toggleSecurity();
    }

    @OnClick(R.id.lay_call_btn)
    void clickCallBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, CallActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.lay_call_record_btn)
    void clickCallRecordBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, CallRecordActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.lay_visitor_list_btn)
    void clickVisitorListBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, VisitHistoryListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.lay_work_record_btn)
    void clickWorkRecordBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, WorkRecordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.lay_change_worker_btn)
    void clickChangeWorkerBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, ChangeWorkerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MAIN);
    }

    @OnClick(R.id.lay_setting_btn)
    void clickSettingBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FIRST_SETTING) {
            if (mPrefGlobal.getAuthorization() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem != null) {
                // initActivity Code
                Intent serviceIntent = new Intent(this, WebSocketService.class);
                serviceIntent.putExtra("deviceBody", Gayo_SharedPreferences.PrefDeviceData.prefItem);
                serviceIntent.putExtra("authorization", mPrefGlobal.getAuthorization());

                startService(serviceIntent);
                getCurrentWorkerInfo();
                getWeatherInfo();
                getFacilityAllContactList();
            } else {
                showWarningDialog("저장된 데이터가 없습니다.\n설정으로 이동하여 세팅 또는\n데이터를 불러와주세요.", getString(R.string.confirm));
            }
            return;
        }

        if (mPrefGlobal.getAuthorization() != null) {
            getWeatherInfo();
            if (resultCode == RESULT_CHANGED_WORKER) {
                getCurrentWorkerInfo();
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;

    }

    @Override
    public void onResume() {
        super.onResume();

        // 날짜가 바뀌면 알림을 받음
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(dateChangeReceiver, filter);
        if (mPrefGlobal.getAuthorization() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem != null) {
            getWeatherInfo();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dateChangeReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(-1);
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {

        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onNextStep() {
                if (mIsError) {
                    mIsError = false;
                    return;
                }
                if (mPrefGlobal.getAuthorization() == null || Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
                    showProgress();
                    Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_FIRST_SETTING);

                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.i(TAG, "Fetching FCM registration token : " + token);

                        if (mPrefGlobal.getFirebaseToken() == null || !mPrefGlobal.getFirebaseToken().equals(token)) {
                            mPrefGlobal.setFirebaseToken(token);

                        }

                    }
                });

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.KOREA);
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("E", Locale.KOREA);

        tvYearAndMonth.setText(yearMonthFormat.format(today));
        tvDayNumber.setText(dayFormat.format(today));
        tvWeekDay.setText("일 (" + weekdayFormat.format(today) + ")");

        if (mPrefGlobal.getAuthorization() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem != null) {
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.putExtra("authorization", mPrefGlobal.getAuthorization());
            serviceIntent.putExtra("deviceBody", Gayo_SharedPreferences.PrefDeviceData.prefItem);
            startService(serviceIntent);

            getDeviceList();
            getFacilityAllContactList();
            getCurrentWorkerInfo();
            getWeatherInfo();
            getPatrolModeInfo();


        } else {
            showWarningDialog("저장된 데이터가 없습니다.\n설정으로 이동하여 세팅 또는 데이터를 불러와주세요.", getString(R.string.confirm));
        }

    }

    private void getPatrolModeInfo() {

        Retrofit.MainInfoApi mainInfoApi = Retrofit.MainInfoApi.retrofit.create(Retrofit.MainInfoApi.class);

        Call<RequestDataFormat.DeviceBody> call = mainInfoApi.patrolModeInfoPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()));
        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                if (response.body() != null) {
                    if (response.body().getPatrolState() != null) {
                        if (response.body().getPatrolState().equalsIgnoreCase("O")) {
                            mPrefGlobal.setPatrolMode(true);
                            ivPatrolBtn.setImageResource(R.drawable.on_icon);
                            tvSecurityState1.setText("경비실기 - 순찰 중");
                            tvSecurityState2.setText("순찰 ON");

                        } else {
                            mPrefGlobal.setPatrolMode(false);
                            ivPatrolBtn.setImageResource(R.drawable.off_icon);
                            tvSecurityState1.setText("경비실기 - 근무 중");
                            tvSecurityState2.setText("순찰 OFF");
                        }
                    }
                } else {
                    mIsError = true;
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                    if (errorMessage != null) {
                        Log.e(TAG, errorMessage);
                        showWarningDialog(errorMessage, getString(R.string.confirm));
                    }
                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {

            }
        });
    }

    private void updatePatrolMode() {
        showProgress();

        Retrofit.MainInfoApi mainInfoApi = Retrofit.MainInfoApi.retrofit.create(Retrofit.MainInfoApi.class);

        Call<RequestDataFormat.DeviceBody> call = mainInfoApi.updatePatrolModePost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), mPrefGlobal.getPatrolMode() ? "O" : "X"));
        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                closeProgress();

                if (response.body() != null) {

                    if (response.body().getResult().equalsIgnoreCase("OK")) {

                    } else {

                    }

                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIsError = true;
                    String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                    Log.e(TAG, errorMessage);
                    showWarningDialog(errorMessage, getString(R.string.confirm));
                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                closeProgress();
            }
        });
    }


    private void getFacilityAllContactList() {

        showProgress();

        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);

        Call<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>> call = facilityContactApi.loadAllContactListPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.ContactBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode(), 1));

        call.enqueue(new Callback<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>>() {
            @Override
            public void onResponse(Call<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>> call, Response<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>> response) {
                if (response.body() != null) {
                    mPrefGlobal.setContactsList(response.body());

                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIsError = true;
                    String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                    Log.e(TAG, errorMessage);
                    showWarningDialog(errorMessage, getString(R.string.confirm));
                }

            }

            @Override
            public void onFailure(Call<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>> call, Throwable t) {

            }
        });

        closeProgress();


    }

    private void updateDateTexts() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy년 MM월", Locale.KOREA);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.KOREA);
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("E", Locale.KOREA);

        tvYearAndMonth.setText(yearMonthFormat.format(today));
        tvDayNumber.setText(dayFormat.format(today));
        tvWeekDay.setText("일 (" + weekdayFormat.format(today) + ")");
    }

    private void getDeviceList() {
        showProgress();

        Retrofit.AddFacilityContactApi facilityContactApi = Retrofit.AddFacilityContactApi.retrofit.create(Retrofit.AddFacilityContactApi.class);

        Call<List<RequestDataFormat.DeviceNetworkBody>> call = facilityContactApi.loadAllDeviceListPost(mPrefGlobal.getAuthorization());
        call.enqueue(new Callback<List<RequestDataFormat.DeviceNetworkBody>>() {
            @Override
            public void onResponse(Call<List<RequestDataFormat.DeviceNetworkBody>> call, Response<List<RequestDataFormat.DeviceNetworkBody>> response) {
                if (response.body() != null) {
//                    if (!response.body().isEmpty()) {}
                    mPrefGlobal.setAllDeviceList(response.body());

                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIsError = true;
                    String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                    Log.e(TAG, errorMessage);
                    showWarningDialog(errorMessage, getString(R.string.confirm));
                }
            }

            @Override
            public void onFailure(Call<List<RequestDataFormat.DeviceNetworkBody>> call, Throwable t) {

            }
        });

    }

    private void getCurrentWorkerInfo() {
        showProgress();
        Retrofit.MainInfoApi mainInfoApi = Retrofit.MainInfoApi.retrofit.create(Retrofit.MainInfoApi.class);

        Call<ResponseDataFormat.CurrentWorkerInfo> call = mainInfoApi.currentWorkerPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()));
        call.enqueue(new Callback<ResponseDataFormat.CurrentWorkerInfo>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.CurrentWorkerInfo> call, Response<ResponseDataFormat.CurrentWorkerInfo> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
                        tvWorkerName.setText(response.body().getWorkerName().isEmpty() ? "근무자" : response.body().getWorkerName());
                        tvWorkerPhoneNumber.setText(response.body().getWorkerPhoneNumber().isEmpty() ? "정보 없음" : response.body().getWorkerPhoneNumber());
                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIsError = true;
                    String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                    Log.e(TAG, errorMessage);
                    showWarningDialog(errorMessage, getString(R.string.confirm));
                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.CurrentWorkerInfo> call, Throwable t) {
                closeProgress();

            }
        });
    }

    private void getWeatherInfo() {
        Retrofit.MainInfoApi mainInfoApi = Retrofit.MainInfoApi.retrofit.create(Retrofit.MainInfoApi.class);

        Call<ResponseDataFormat.WeatherData> call = mainInfoApi.weatherInfoPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.WeatherBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode())); // test
        call.enqueue(new Callback<ResponseDataFormat.WeatherData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.WeatherData> call, Response<ResponseDataFormat.WeatherData> response) {
                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
                        int weatherNumber = response.body().getWeatherConditions();
                        setWeatherImg(weatherNumber);
                        tvTemperature.setText(String.valueOf(response.body().getCurrentTemp()));
                        tvRegionInfo.setText(response.body().getAreaName());
                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIsError = true;
                    String errorMessage = BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", "");
                    Log.e(TAG, errorMessage);
                    showWarningDialog(errorMessage, getString(R.string.confirm));
                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.WeatherData> call, Throwable t) {

            }
        });

    }

    private void toggleSecurity() {
    }

    private void setWeatherImg(int weatherNumber) {

        switch (weatherNumber) {
            case 2:
            case 3:
                ivWeatherImg.setImageResource(R.drawable.weather_02);
                ivWeatherImg.setImageResource(R.drawable.weather_02);
                break;
            case 4:
                ivWeatherImg.setImageResource(R.drawable.weather_05);
                break;
            case 5:
                ivWeatherImg.setImageResource(R.drawable.weather_03);
                break;
            case 7:
            case 11:
                ivWeatherImg.setImageResource(R.drawable.weather_07);
                break;
            case 8:
            case 9:

                ivWeatherImg.setImageResource(R.drawable.weather_06);
                break;
            case 6:
            case 10:
                ivWeatherImg.setImageResource(R.drawable.weather_04);
                break;
            default:
                ivWeatherImg.setImageResource(R.drawable.weather_01);
                break;
        }
    }
}
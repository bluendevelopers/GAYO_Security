package bluen.homein.gayo_security;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.ui.AppBarConfiguration;

import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.databinding.ActivityMainBinding;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

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
    @BindView(R.id.iv_power_btn)
    ImageView ivPowerBtn;
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

    @OnClick(R.id.lay_back_btn)
    void clickBackBtn() {
        onBackPressed();
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.iv_power_btn)
    void clickWorkPowerBtn() {
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
        Intent intent = new Intent(MainActivity.this, VisitorListActivity.class);
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
        startActivity(intent);
    }

    @OnClick(R.id.lay_setting_btn)
    void clickSettingBtn() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
        startActivity(intent);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;

    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        Retrofit.MainInfoApi mainInfoApi = Retrofit.MainInfoApi.retrofit.create(Retrofit.MainInfoApi.class);

        getCurrentWorkerInfo(mainInfoApi);
        getWeatherInfo(mainInfoApi);

    }

    private void getCurrentWorkerInfo(Retrofit.MainInfoApi mainInfoApi) {
        showProgress();

        Call<ResponseDataFormat.CurrentWorkerData> call = mainInfoApi.currentWorkerPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(serialCode, buildingCode)); // test
        call.enqueue(new Callback<ResponseDataFormat.CurrentWorkerData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.CurrentWorkerData> call, Response<ResponseDataFormat.CurrentWorkerData> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
                        tvWorkerName.setText(response.body().getWorkerName());
                        tvWorkerPhoneNumber.setText(response.body().getWorkerPhoneNumber());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.CurrentWorkerData> call, Throwable t) {
                closeProgress();

            }
        });
    }

    private void getWeatherInfo(Retrofit.MainInfoApi mainInfoApi) {
        showProgress();

        Call<ResponseDataFormat.WeatherData> call = mainInfoApi.weatherInfoPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.WeatherBody(buildingCode)); // test
        call.enqueue(new Callback<ResponseDataFormat.WeatherData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.WeatherData> call, Response<ResponseDataFormat.WeatherData> response) {
                closeProgress();
                if (response.body() != null) {
                    if (response.body().getMessage() == null) {
                        int weatherNumber = response.body().getWeatherConditions();
                        setWeatherImg(weatherNumber);
                        tvTemperature.setText(String.valueOf(response.body().getCurrentTemp()));
                        tvRegionInfo.setText(response.body().getAreaName());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.WeatherData> call, Throwable t) {
                closeProgress();

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
package bluen.homein.gayo_security.activity.preferences.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataSetFragment extends BaseFragment {
    private AudioManager am;

    @OnClick(R.id.tv_load_data_btn)
    void getDeviceData() {
        activity.showProgress();

        Retrofit.PreferencesApi loginApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<RequestDataFormat.DeviceBody> call = loginApi.loadDeviceDataPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode())); // test

        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult() == null) {
                        RequestDataFormat.DeviceBody body = response.body();
                        if (body.getDeviceSleepModeBody().getMainReturnTime() != 0 || body.getDeviceSleepModeBody().getMainReturnTime() != 30 || body.getDeviceSleepModeBody().getMainReturnTime() != 60
                                || body.getDeviceSleepModeBody().getMainReturnTime() != 90) {
                            body.getDeviceSleepModeBody().setMainReturnTime(0);
                        }

                        if (body.getDeviceSleepModeBody().getMainReturnTime() != 0 || body.getDeviceSleepModeBody().getMainReturnTime() != 10 || body.getDeviceSleepModeBody().getMainReturnTime() != 30
                                || body.getDeviceSleepModeBody().getMainReturnTime() != 60 || body.getDeviceSleepModeBody().getMainReturnTime() != 90) {
                            body.getDeviceSleepModeBody().setSleepTime(0);
                        }
                        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, body);

                        am.setStreamVolume(
                                AudioManager.STREAM_RING,
                                body.getDeviceSoundBody().getCallSound(),
                                0
                        );
                        am.setStreamVolume(
                                AudioManager.STREAM_ALARM,
                                body.getDeviceSoundBody().getAlarmSound(),
                                0
                        );
                        am.setStreamVolume(
                                AudioManager.STREAM_SYSTEM,
                                body.getDeviceSoundBody().getSystemSound(),
                                0
                        );
                        activity.showPopupDialog(null, "데이터 불러오기에\n성공하였습니다.",  getString(R.string.confirm));

                    } else {

                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                activity.closeProgress();

            }
        });
    }

    @OnClick(R.id.tv_export_data_btn)
    void exportDeviceData() {

        if (Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
            return;
        }

        if (mPrefGlobal.getFirebaseToken() == null) {
            return;
        }

        if (mPrefGlobal.getFirebaseToken() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceToken() == null) {
            Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceToken(mPrefGlobal.getFirebaseToken());
            Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
        }

        activity.showProgress();

        Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<ResponseDataFormat.SavePreferencesData> call = preferencesApi.exportPreferencesPost(mPrefGlobal.getAuthorization(), Gayo_SharedPreferences.PrefDeviceData.prefItem);

        call.enqueue(new Callback<ResponseDataFormat.SavePreferencesData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.SavePreferencesData> call, Response<ResponseDataFormat.SavePreferencesData> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        activity.showPopupDialog(null, "데이터 내보내기에\n성공하였습니다.",  getString(R.string.confirm));
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.SavePreferencesData> call, Throwable t) {
                activity.closeProgress();

            }
        });


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_data_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {
        am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

    }


}

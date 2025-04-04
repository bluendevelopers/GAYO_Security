package bluen.homein.gayo_security.activity.preferences.fragment;

import android.view.View;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
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

    @OnClick(R.id.tv_load_data_btn)
    void getDeviceData() {
        ((PreferencesActivity) activity).showProgress();

        Retrofit.PreferencesApi loginApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<RequestDataFormat.DeviceBody> call = loginApi.loadDeviceDataPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(serialCode, buildingCode)); // test

        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                ((PreferencesActivity) activity).closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult() == null) {
                        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, response.body());
                        ((PreferencesActivity) activity).showPopupDialog(null, "데이터 불러 오기에 성공하였습니다.", "확 인");

                    } else {

                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                ((PreferencesActivity) activity).closeProgress();

            }
        });
    }

    @OnClick(R.id.tv_export_data_btn)
    void exportDeviceData() {
        ((PreferencesActivity) activity).showProgress();

        Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<ResponseDataFormat.SavePreferencesData> call = preferencesApi.exportPreferencesPost(mPrefGlobal.getAuthorization(), Gayo_SharedPreferences.PrefDeviceData.prefItem);

        call.enqueue(new Callback<ResponseDataFormat.SavePreferencesData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.SavePreferencesData> call, Response<ResponseDataFormat.SavePreferencesData> response) {
                ((PreferencesActivity) activity).closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        ((PreferencesActivity) activity).showPopupDialog(null, "데이터 내보내기에 성공하였습니다.", "확 인");
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.SavePreferencesData> call, Throwable t) {
                ((PreferencesActivity) activity).closeProgress();

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


    }


}

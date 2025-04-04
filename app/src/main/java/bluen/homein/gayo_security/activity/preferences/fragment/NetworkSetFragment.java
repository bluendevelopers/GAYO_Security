package bluen.homein.gayo_security.activity.preferences.fragment;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;
import bluen.homein.gayo_security.rest.Retrofit;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkSetFragment extends BaseFragment implements PreferencesActivity.NetworkInterface {

    @BindView(R.id.et_dummy)
    EditText etDummy;
    @BindView(R.id.et_building_code)
    EditText etBuildingCode;
    @BindView(R.id.et_serial_code)
    EditText etSerialCode;

    @BindView(R.id.et_device_name)
    EditText etDeviceName;

    @BindView(R.id.et_ip_address)
    EditText etIpAddress;

    @BindView(R.id.et_mac_address)
    EditText etMacAddress;

    @BindView(R.id.et_subnet_mask)
    EditText etSubnetMask;

    @BindView(R.id.et_gateway)
    EditText etGateway;

    @BindView(R.id.et_server_ip)
    EditText etServerIp;

    @BindView(R.id.et_server_port)
    EditText etServerPort;
    @BindView(R.id.tv_cancel)
    TextView tvCancelBtn;
    @BindView(R.id.tv_save_btn)
    TextView tvSaveBtn;
    @BindView(R.id.lv_allow_ip)
    ListView lvAllowIp;

    AllowedIpListAdapter allowedIpListAdapter;
    List<RequestDataFormat.IpAddressBody> tempAllowedIpList = new ArrayList<>();

    @OnClick(R.id.tv_load_data_btn)
    void clickLoadDataBtn() {

        if (etBuildingCode.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "건물 번호를 입력해주세요.", "확 인");
            return;
        }

        if (etSerialCode.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "시리얼 코드를 입력해주세요.", "확 인");
            return;
        }

        if (mPrefGlobal.getAuthorization() == null) {
            ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_GET_TOKEN);
            activity.showPopupDialog(null, "입력하신 값을 바탕으로\n서버 내 데이터 유무 확인 후\n설정 데이터를 불러옵니다.", "취 소", "확 인");

//            getToken();
        } else {
            ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_GET_NETWORK_DATA);
            activity.showPopupDialog(null, "입력하신 정보를 바탕으로\n서버에 저장된 네트워크 정보를 불러옵니다.\n(주의: 저장 되지 않은 값은 유실됩니다.)", "취 소", "확 인");
//            getNetworkData();
        }


    }

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {


        if (etBuildingCode.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "건물 번호를 입력해주세요.", "확 인");
            return;
        }
        if (etSerialCode.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "시리얼 코드를 입력해주세요.", "확 인");
            return;
        }

        if (mPrefGlobal.getAuthorization() == null || Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
            ((PreferencesActivity) activity).showPopupDialog(null, "먼저, 하단의\n'설정 데이터 불러오기' 버튼을 눌러\n서버 데이터를 확인해주세요.", "확 인");
            return;
        }

        if (etDeviceName.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "기기 명을 입력해주세요.", "확 인");
            return;
        }

        if (etIpAddress.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "IP 주소를 입력해주세요.", "확 인");
            return;
        }

        if (etMacAddress.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "MAC 주소를 입력해주세요.", "확 인");
            return;
        }


        if (etSubnetMask.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "Subnet을 입력해주세요.", "확 인");
            return;
        }
        if (etGateway.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "Gateway를 입력해주세요.", "확 인");
            return;
        }

        if (etServerIp.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "서버 IP를 입력해주세요.", "확 인");
            return;
        }

        if (etServerPort.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "서버 Port 를 입력해주세요.", "확 인");
        }

        allowedIpListAdapter.checkBlankItem();

        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_SAVE_DATA);
        activity.showWarningDialog("설정 값을 저장하시겠습니까?\n저장 시 기존 데이터를 덮어 씁니다.", "아니오", "예");

    }

    @OnClick(R.id.tv_cancel)
    void clickCancelBtn() {
        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_REFRESH_NUMBER);
        activity.showWarningDialog("수정을 취소하시겠습니까?\n변경 값이 원래 값으로 초기화 됩니다.", "아니오", "예");
    }

    @OnClick({R.id.lay_root, R.id.lay_scrollView, R.id.scrollView})
    void clickLayRoot() {
        hideAndClearFocus(appContext, etDeviceName);
        hideAndClearFocus(appContext, etIpAddress);
        hideAndClearFocus(appContext, etMacAddress);
        hideAndClearFocus(appContext, etSubnetMask);
        hideAndClearFocus(appContext, etGateway);
        hideAndClearFocus(appContext, etServerIp);
        hideAndClearFocus(appContext, etServerPort);
        etDummy.requestFocus();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_network_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {

        if (mPrefGlobal.getAuthorization() != null && Gayo_SharedPreferences.PrefDeviceData.prefItem != null) {
            RequestDataFormat.DeviceNetworkBody deviceNetworkBody = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceNetworkBody();

            if (deviceNetworkBody != null) {
                etBuildingCode.setText(Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode());
                etSerialCode.setText(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode());
                etDeviceName.setText(deviceNetworkBody.getFacilityName());
                etIpAddress.setText(deviceNetworkBody.getIpAddress());
                etMacAddress.setText(deviceNetworkBody.getMacAddress());
                etSubnetMask.setText(deviceNetworkBody.getSubNet());
                etGateway.setText(deviceNetworkBody.getGateWayIP());
                etServerIp.setText(deviceNetworkBody.getServerIP());
                etServerPort.setText(String.valueOf(deviceNetworkBody.getServerPort()));

                tvCancelBtn.setVisibility(View.VISIBLE);

                tempAllowedIpList.clear(); // 초기화
                if (deviceNetworkBody.getAllowedIpList() != null) {
                    for (RequestDataFormat.IpAddressBody ipBody : deviceNetworkBody.getAllowedIpList()) {
                        tempAllowedIpList.add(new RequestDataFormat.IpAddressBody(ipBody.getIpAddress()));
                    }

                    allowedIpListAdapter = new AllowedIpListAdapter(activity, tempAllowedIpList);
                    lvAllowIp.setAdapter(allowedIpListAdapter);
                }

            }
        } else {
            tvSaveBtn.setText("세팅 하기");
            tvCancelBtn.setVisibility(View.INVISIBLE);
            tempAllowedIpList.clear(); // 초기화
            tempAllowedIpList.add(new RequestDataFormat.IpAddressBody("..."));
            allowedIpListAdapter = new AllowedIpListAdapter(activity, tempAllowedIpList);
            lvAllowIp.setAdapter(allowedIpListAdapter);
        }
        hideAndClearFocus(appContext, etDummy);
    }

    @Override
    public void refreshFragment() {
        initFragmentView(getView());
    }

    @Override
    public void saveData() {
        ArrayList<RequestDataFormat.IpAddressBody> ipAddressList = new ArrayList<>();

        if (!allowedIpListAdapter.itemsList.isEmpty()) {
            for (RequestDataFormat.IpAddressBody item : allowedIpListAdapter.itemsList) {
                if (!item.getIpAddress().equals("...")) {
                    ipAddressList.add(item);
                }
            }
        }

        RequestDataFormat.DeviceNetworkBody newDeviceNetworkBody = new RequestDataFormat.DeviceNetworkBody(
                EditTextToString(etDeviceName),
                EditTextToString(etIpAddress),
                EditTextToString(etMacAddress),
                EditTextToString(etSubnetMask),
                EditTextToString(etGateway),
                EditTextToString(etServerIp),
                Integer.parseInt(EditTextToString(etServerPort)),
                ipAddressList
        );

        if (Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
            // 최초로 모든 설정 저장하는 api
            Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext
                    , new RequestDataFormat.DeviceBody(EditTextToString(etSerialCode), EditTextToString(etBuildingCode)
                            , new RequestDataFormat.DeviceUIBody(50, "L")
                            , new RequestDataFormat.DeviceSoundBody(5, "01", 5, "01", 5, "01", 5, 5)
                            , new RequestDataFormat.DeviceSleepModeBody(0, 0),
                            newDeviceNetworkBody));
            Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceNetworkBody(newDeviceNetworkBody);
            Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
            saveDeviceData();
        } else {
            //네트워크만 서버에 저장하는 api
            saveNetworkData();
        }

    }

    @Override
    public void getNetworkData() {
        Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<RequestDataFormat.DeviceBody> call = preferencesApi.loadNetworkSettingPost(mPrefGlobal.getAuthorization(),
                new RequestDataFormat.DeviceInfoBody(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode(), Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()));

        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult() == null) {

                        RequestDataFormat.DeviceNetworkBody deviceNetworkBody = response.body().getDeviceNetworkBody();

                        if (deviceNetworkBody != null) {
                            etBuildingCode.setText(Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode());
                            etSerialCode.setText(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode());
                            etDeviceName.setText(deviceNetworkBody.getFacilityName());
                            etIpAddress.setText(deviceNetworkBody.getIpAddress());
                            etMacAddress.setText(deviceNetworkBody.getMacAddress());
                            etSubnetMask.setText(deviceNetworkBody.getSubNet());
                            etGateway.setText(deviceNetworkBody.getGateWayIP());
                            etServerIp.setText(deviceNetworkBody.getServerIP());
                            etServerPort.setText(String.valueOf(deviceNetworkBody.getServerPort()));

                            tempAllowedIpList.clear(); // 초기화
                            if (deviceNetworkBody.getAllowedIpList() != null) {
                                for (RequestDataFormat.IpAddressBody ipBody : deviceNetworkBody.getAllowedIpList()) {
                                    tempAllowedIpList.add(new RequestDataFormat.IpAddressBody(ipBody.getIpAddress()));
                                }

                                allowedIpListAdapter = new AllowedIpListAdapter(activity, tempAllowedIpList);
                                lvAllowIp.setAdapter(allowedIpListAdapter);
                            }
                            ((PreferencesActivity) activity).showPopupDialog(null, "설정 데이터 불러오기에\n성공하였습니다.", "확 인");

                        }

                    } else {
                        ((PreferencesActivity) activity).showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");
                    }
                } else {
                    ((PreferencesActivity) activity).showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");

                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                activity.closeProgress();

            }
        });
    }

    @Override
    public void getToken() {
        activity.showProgress();

        Retrofit.LoginApi loginInterface = Retrofit.LoginApi.retrofit.create(Retrofit.LoginApi.class);

        Call<ResponseDataFormat.LoginData> call = loginInterface.getAuthInfo(new RequestDataFormat.DeviceInfoBody(etSerialCode.getText().toString(), etBuildingCode.getText().toString(), "00-00-00-00", "192.168.0.102"));
        call.enqueue(new Callback<ResponseDataFormat.LoginData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.LoginData> call, Response<ResponseDataFormat.LoginData> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getAuthorization() != null) {
                        mPrefGlobal.setAuthorization(response.body().getAuthorization());
                        Gayo_SharedPreferences.PrefDeviceData.initPrefDeviceData(appContext);
                        if (Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
                            getDeviceData();
                        } else {
                            getNetworkData();
                        }
                    } else {
                        //code
                    }
                } else {
                    //code
                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.LoginData> call, Throwable t) {
                activity.closeProgress();

            }
        });
    }

    private void saveNetworkData() {

        //api 나온 후 가능
    }

    private void saveDeviceData() {
        ((PreferencesActivity) activity).showProgress();

        Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<ResponseDataFormat.SavePreferencesData> call = preferencesApi.exportPreferencesPost(mPrefGlobal.getAuthorization(), Gayo_SharedPreferences.PrefDeviceData.prefItem);

        call.enqueue(new Callback<ResponseDataFormat.SavePreferencesData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.SavePreferencesData> call, Response<ResponseDataFormat.SavePreferencesData> response) {
                ((PreferencesActivity) activity).closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_SETTING_SUCCESS_NUMBER);
                        ((PreferencesActivity) activity).showPopupDialog(null, "기기 세팅이 완료되었습니다.", "확 인");
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

    private void getDeviceData() {
        Retrofit.PreferencesApi loginApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<RequestDataFormat.DeviceBody> call = loginApi.loadDeviceDataPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(serialCode, buildingCode)); // test

        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult() == null) {
                        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, response.body());
                        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_SETTING_SUCCESS_NUMBER);
                        ((PreferencesActivity) activity).showPopupDialog(null, "설정 데이터 불러오기에 성공하였습니다.", "확 인");

                    } else {
                        ((PreferencesActivity) activity).showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");
                    }
                } else {
                    ((PreferencesActivity) activity).showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");

                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                activity.closeProgress();

            }
        });
    }


    private String EditTextToString(EditText et) {
        return et.getText().toString().trim();
    }
}

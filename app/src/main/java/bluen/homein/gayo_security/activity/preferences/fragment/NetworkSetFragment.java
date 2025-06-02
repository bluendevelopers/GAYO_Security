package bluen.homein.gayo_security.activity.preferences.fragment;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseActivity;
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

    @BindView(R.id.et_ip_address_1)
    EditText etIpAddress1;
    @BindView(R.id.et_ip_address_2)
    EditText etIpAddress2;
    @BindView(R.id.et_ip_address_3)
    EditText etIpAddress3;
    @BindView(R.id.et_ip_address_4)
    EditText etIpAddress4;

    @BindView(R.id.et_mac_address_1)
    EditText etMacAddress1;
    @BindView(R.id.et_mac_address_2)
    EditText etMacAddress2;
    @BindView(R.id.et_mac_address_3)
    EditText etMacAddress3;
    @BindView(R.id.et_mac_address_4)
    EditText etMacAddress4;
    @BindView(R.id.et_mac_address_5)
    EditText etMacAddress5;
    @BindView(R.id.et_mac_address_6)
    EditText etMacAddress6;

    @BindView(R.id.et_gateway_1)
    EditText etGateway1;
    @BindView(R.id.et_gateway_2)
    EditText etGateway2;
    @BindView(R.id.et_gateway_3)
    EditText etGateway3;
    @BindView(R.id.et_gateway_4)
    EditText etGateway4;

    @BindView(R.id.et_server_ip_1)
    EditText etServerIp1;
    @BindView(R.id.et_server_ip_2)
    EditText etServerIp2;
    @BindView(R.id.et_server_ip_3)
    EditText etServerIp3;
    @BindView(R.id.et_server_ip_4)
    EditText etServerIp4;

    @BindView(R.id.et_subnet_mask_1)
    EditText etSubnetMask1;
    @BindView(R.id.et_subnet_mask_2)
    EditText etSubnetMask2;
    @BindView(R.id.et_subnet_mask_3)
    EditText etSubnetMask3;
    @BindView(R.id.et_subnet_mask_4)
    EditText etSubnetMask4;


    @BindView(R.id.et_server_port)
    EditText etServerPort;
    @BindView(R.id.tv_save_btn)
    TextView tvSaveBtn;
    @BindView(R.id.lv_allow_ip)
    ListView lvAllowIp;

    AllowedIpListAdapter allowedIpListAdapter;
    List<RequestDataFormat.IpAddressBody> tempAllowedIpList = new ArrayList<>();

    String TAG = "NetworkSetFragment";

    @OnClick(R.id.tv_load_data_btn)
    void clickLoadDataBtn() {

        if (etBuildingCode.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "건물 번호를 입력해주세요.", "확 인");
            return;
        }

        if (etSerialCode.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "시리얼 코드를 입력해주세요.", "확 인");
            return;
        }


        if (Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
            ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_GET_TOKEN);
            activity.showPopupDialog(null, "입력하신 값을 바탕으로\n서버 내 데이터 유무 확인 후\n설정 데이터를 불러옵니다.", "취 소", "확 인");
        } else {
            if (!etBuildingCode.equals(Gayo_SharedPreferences.PrefDeviceData.prefItem.getBuildingCode()) || !etSerialCode.equals(Gayo_SharedPreferences.PrefDeviceData.prefItem.getSerialCode())) {
                ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_GET_TOKEN);
                activity.showPopupDialog(null, "입력하신 값을 바탕으로\n서버 내 데이터 유무 확인 후\n설정 데이터를 불러옵니다.", "취 소", "확 인");
            } else {
                ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_GET_NETWORK_DATA);
                activity.showPopupDialog(null, "입력하신 정보를 바탕으로\n서버에 저장된 네트워크 정보를 불러옵니다.\n(주의: 저장 되지 않은 값은 유실됩니다.)", "취 소", "확 인");
            }
        }

    }

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {


        if (etBuildingCode.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "건물 번호를 입력해주세요.", "확 인");
            return;
        }
        if (etSerialCode.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "시리얼 코드를 입력해주세요.", "확 인");
            return;
        }

        if (mPrefGlobal.getAuthorization() == null || Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
            activity.showPopupDialog(null, "먼저, 스크롤 하단의\n'설정 데이터 불러오기' 버튼을 눌러\n서버 데이터를 확인해주세요.", "확 인");
            return;
        }

        if (etDeviceName.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "기기 명을 입력해주세요.", "확 인");
            return;
        }

        if (etIpAddress1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "IP 주소를 입력해주세요.", "확 인");
            return;
        }

        if (etMacAddress1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "MAC 주소를 입력해주세요.", "확 인");
            return;
        }


        if (etSubnetMask1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "Subnet을 입력해주세요.", "확 인");
            return;
        }
        if (etGateway1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "Gateway를 입력해주세요.", "확 인");
            return;
        }

        if (etServerIp1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "서버 IP를 입력해주세요.", "확 인");
            return;
        }

        if (etServerPort.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "서버 Port 를 입력해주세요.", "확 인");
        }

        allowedIpListAdapter.checkBlankItem();

        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_SAVE_DATA);
        activity.showWarningDialog("설정 값을 저장하시겠습니까?\n저장 시 기존 데이터를 덮어 씁니다.", "아니오", "예");

    }

    @OnClick(R.id.tv_cancel)
    void clickCancelBtn() {
        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_REFRESH);
        activity.showWarningDialog("수정을 취소하시겠습니까?\n변경 값이 원래 값으로 초기화 됩니다.", "아니오", "예");
    }

    @OnClick({R.id.lay_root, R.id.lay_scrollView, R.id.scrollView})
    void clickLayRoot() {
        hideAndClearFocus(appContext, etDeviceName);
        hideAndClearFocus(appContext, etIpAddress1);
        hideAndClearFocus(appContext, etMacAddress1);
        hideAndClearFocus(appContext, etSubnetMask1);
        hideAndClearFocus(appContext, etGateway1);
        hideAndClearFocus(appContext, etServerIp1);
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

                if (!deviceNetworkBody.getIpAddress().isEmpty()) {
                    String[] parts = deviceNetworkBody.getIpAddress().split("\\.", -1);  // 추후 사용할 형태로 검사 (-1로 빈칸 포함)
                    if (parts.length == 4) {
                        etIpAddress1.setText(parts[0]);
                        etIpAddress2.setText(parts[1]);
                        etIpAddress3.setText(parts[2]);
                        etIpAddress4.setText(parts[3]);
                    }
                }

                if (!deviceNetworkBody.getSubNet().isEmpty()) {
                    String[] parts = deviceNetworkBody.getSubNet().split("\\.", -1);  // 추후 사용할 형태로 검사 (-1로 빈칸 포함)
                    if (parts.length == 4) {
                        etSubnetMask1.setText(parts[0]);
                        etSubnetMask2.setText(parts[1]);
                        etSubnetMask3.setText(parts[2]);
                        etSubnetMask4.setText(parts[3]);
                    }
                }

                if (!deviceNetworkBody.getGateWayIP().isEmpty()) {
                    String[] parts = deviceNetworkBody.getSubNet().split("\\.", -1);  // 추후 사용할 형태로 검사 (-1로 빈칸 포함)
                    if (parts.length == 4) {
                        etGateway1.setText(parts[0]);
                        etGateway2.setText(parts[1]);
                        etGateway3.setText(parts[2]);
                        etGateway4.setText(parts[3]);
                    }
                }

                if (!deviceNetworkBody.getServerIP().isEmpty()) {
                    String[] parts = deviceNetworkBody.getServerIP().split("\\.", -1);  // 추후 사용할 형태로 검사 (-1로 빈칸 포함)
                    if (parts.length == 4) {
                        etServerIp1.setText(parts[0]);
                        etServerIp2.setText(parts[1]);
                        etServerIp3.setText(parts[2]);
                        etServerIp4.setText(parts[3]);
                    }
                }

                etServerPort.setText(String.valueOf(deviceNetworkBody.getServerPort()));

                tempAllowedIpList.clear(); // 초기화

                if (deviceNetworkBody.getAllowedIpList() != null) {
                    for (RequestDataFormat.IpAddressBody ipBody : deviceNetworkBody.getAllowedIpList()) {
                        if (!ipBody.getIpAddress().isEmpty()) {
                            tempAllowedIpList.add(new RequestDataFormat.IpAddressBody(ipBody.getIpAddress()));
                        }
                    }
                    if (tempAllowedIpList.isEmpty()) {
                        tempAllowedIpList.add(new RequestDataFormat.IpAddressBody(""));
                    }
                    allowedIpListAdapter = new AllowedIpListAdapter(activity, tempAllowedIpList);
                    lvAllowIp.setAdapter(allowedIpListAdapter);
                }

            }


        } else {
            tvSaveBtn.setText("세팅 하기");
            tempAllowedIpList.clear(); // 초기화
            tempAllowedIpList.add(new RequestDataFormat.IpAddressBody("..."));
            allowedIpListAdapter = new AllowedIpListAdapter(activity, tempAllowedIpList);
            lvAllowIp.setAdapter(allowedIpListAdapter);
        }

        String macAddress = getMacAddress(getApplicationContext());
        Log.d("MAC", "Device MAC: " + macAddress);

        String[] parts = macAddress.split(":", -1);  // 추후 사용할 형태로 검사 (-1로 빈칸 포함)
        if (!macAddress.equals("02:00:00:00:00:00")) {
            if (parts.length == 6) {
                etMacAddress1.setText(parts[0]);
                etMacAddress2.setText(parts[1]);
                etMacAddress3.setText(parts[2]);
                etMacAddress4.setText(parts[3]);
                etMacAddress5.setText(parts[4]);
                etMacAddress6.setText(parts[5]);

                etMacAddress1.setFocusable(false);
                etMacAddress2.setFocusable(false);
                etMacAddress3.setFocusable(false);
                etMacAddress4.setFocusable(false);
                etMacAddress5.setFocusable(false);
                etMacAddress6.setFocusable(false);
            }
        }

        hideAndClearFocus(appContext, etDummy);
    }

    @Override
    public void refreshFragment() {
        initFragmentView(getView());
    }

    @Override
    public void saveData() {
        ArrayList<RequestDataFormat.IpAddressBody> _ipAddressList = new ArrayList<>();

        if (!allowedIpListAdapter.itemsList.isEmpty()) {
            for (RequestDataFormat.IpAddressBody item : allowedIpListAdapter.itemsList) {
//                if (!item.getIpAddress().equals("...")) {
//                    ipAddressList.add(item);
//                }
                if (!item.getIpAddress().isEmpty() && !item.getIpAddress().equals("...")) {
                    _ipAddressList.add(item);
                }
            }
        }

        RequestDataFormat.DeviceNetworkBody newDeviceNetworkBody = new RequestDataFormat.DeviceNetworkBody(
                EditTextToString(etDeviceName),
                EditTextToString(etIpAddress1),
                EditTextToString(etMacAddress1),
                EditTextToString(etSubnetMask1),
                EditTextToString(etGateway1),
                EditTextToString(etServerIp1),
                Integer.parseInt(EditTextToString(etServerPort)),
                _ipAddressList
        );

        if (mPrefGlobal.getFirebaseToken() == null) {
            activity.showWarningDialog("설정 데이터 저장에 실패하였습니다.\n(토큰 정보 없음)", "확 인");
            return;
        }

        if (Gayo_SharedPreferences.PrefDeviceData.prefItem == null) {
            // 최초로 모든 설정 저장하는 api
            Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext
                    , new RequestDataFormat.DeviceBody(EditTextToString(etSerialCode), EditTextToString(etBuildingCode), mPrefGlobal.getFirebaseToken()
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
                            etIpAddress1.setText(deviceNetworkBody.getIpAddress());
                            etMacAddress1.setText(deviceNetworkBody.getMacAddress());
                            etSubnetMask1.setText(deviceNetworkBody.getSubNet());
                            etGateway1.setText(deviceNetworkBody.getGateWayIP());
                            etServerIp1.setText(deviceNetworkBody.getServerIP());
                            etServerPort.setText(String.valueOf(deviceNetworkBody.getServerPort()));

                            tempAllowedIpList.clear(); // 초기화
                            if (deviceNetworkBody.getAllowedIpList() != null) {
                                for (RequestDataFormat.IpAddressBody ipBody : deviceNetworkBody.getAllowedIpList()) {
                                    tempAllowedIpList.add(new RequestDataFormat.IpAddressBody(ipBody.getIpAddress()));
                                }

                                allowedIpListAdapter = new AllowedIpListAdapter(activity, tempAllowedIpList);
                                lvAllowIp.setAdapter(allowedIpListAdapter);
                            }

                            Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceNetworkBody(deviceNetworkBody);
                            Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
                            activity.showPopupDialog(null, "설정 데이터 불러오기에\n성공하였습니다.", "확 인");

                        }

                    } else {
                        activity.showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");
                    }
                } else {
                    activity.showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");

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

        if (etBuildingCode.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "건물 번호를 입력해주세요.", "확 인");
            return;
        }
        if (etSerialCode.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "시리얼 코드를 입력해주세요.", "확 인");
            return;
        }

        if (etIpAddress1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "IP 주소를 입력해주세요.", "확 인");
            return;
        }

        if (etMacAddress1.getText().toString().isEmpty()) {
            activity.showPopupDialog(null, "MAC 주소를 입력해주세요.", "확 인");
            return;
        }

        String _ipAddress = EditTextToString(etIpAddress1) + "." + EditTextToString(etIpAddress2) + "." + EditTextToString(etIpAddress3) + "." + EditTextToString(etIpAddress4);
        String _macAddress = EditTextToString(etMacAddress1) + ":" + EditTextToString(etMacAddress2) + ":" + EditTextToString(etMacAddress3) + ":" + EditTextToString(etMacAddress4) + EditTextToString(etMacAddress5) + EditTextToString(etMacAddress6);

        activity.showProgress();

        String fcmToken = mPrefGlobal.getFirebaseToken();

        mPrefGlobal.removeAll();

        if (fcmToken != null) {
            mPrefGlobal.setFirebaseToken(fcmToken);
        }

        Retrofit.LoginApi loginInterface = Retrofit.LoginApi.retrofit.create(Retrofit.LoginApi.class);

        Call<ResponseDataFormat.LoginData> call = loginInterface.getAuthInfo(
                new RequestDataFormat.DeviceInfoBody(etSerialCode.getText().toString(), etBuildingCode.getText().toString(),
                        _macAddress, _ipAddress));
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
//                        {"result":"E01","message":"등록된 디바이스가 아니거라, 중지된 디바이스입니다."}
                        //code
                    }
                } else if (response.errorBody() != null) {
                    String error = "";
                    try {
                        error = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, BaseActivity.ErrorBodyParser.JsonParser(error)[BaseActivity.ErrorBodyParser.ERROR_MESSAGE_NUM].replace("\"", ""));
                    activity.showWarningDialog("해당 정보로 등록된 기기가 존재하지 않습니다.", getString(R.string.confirm));

                }
            }

            @Override
            public void onFailure(Call<ResponseDataFormat.LoginData> call, Throwable t) {
                activity.closeProgress();

            }
        });
    }

    private void saveNetworkData() {

        activity.showProgress();

        ArrayList<RequestDataFormat.IpAddressBody> _ipAddressList = new ArrayList<>();

        if (!allowedIpListAdapter.itemsList.isEmpty()) {
            for (RequestDataFormat.IpAddressBody item : allowedIpListAdapter.itemsList) {
                if (!item.getIpAddress().isEmpty() && !item.getIpAddress().equals("...")) {
                    _ipAddressList.add(item);
                }
            }
        }

        String _ipAddress = EditTextToString(etIpAddress1) + "." + EditTextToString(etIpAddress2) + "." + EditTextToString(etIpAddress3) + "." + EditTextToString(etIpAddress4);
        String _macAddress = EditTextToString(etMacAddress1) + ":" + EditTextToString(etMacAddress2) + ":" + EditTextToString(etMacAddress3) + ":" + EditTextToString(etMacAddress4) + ":" + EditTextToString(etMacAddress5) + ":" + EditTextToString(etMacAddress6);
        String _gateway = EditTextToString(etGateway1) + "." + EditTextToString(etGateway2) + "." + EditTextToString(etGateway3) + "." + EditTextToString(etGateway4);
        String _subnet = EditTextToString(etSubnetMask1) + "." + EditTextToString(etSubnetMask2) + "." + EditTextToString(etSubnetMask3) + "." + EditTextToString(etSubnetMask4);
        String _serverIp = EditTextToString(etServerIp1) + "." + EditTextToString(etServerIp2) + "." + EditTextToString(etServerIp3) + "." + EditTextToString(etServerIp4);
        Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        RequestDataFormat.DeviceNetworkBody deviceNetworkBody = new RequestDataFormat.DeviceNetworkBody(EditTextToString(etSerialCode), EditTextToString(etBuildingCode), EditTextToString(etDeviceName)
                , _ipAddress, _macAddress, _gateway, _subnet, _serverIp, Integer.parseInt(EditTextToString(etServerPort)), _ipAddressList);

        Call<RequestDataFormat.DeviceBody> call = preferencesApi.saveNetworkData(mPrefGlobal.getAuthorization(), deviceNetworkBody);

        call.enqueue(new Callback<RequestDataFormat.DeviceBody>() {
            @Override
            public void onResponse(Call<RequestDataFormat.DeviceBody> call, Response<RequestDataFormat.DeviceBody> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceNetworkBody(deviceNetworkBody);
                        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
                        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_REFRESH);
                        activity.showPopupDialog(null, "성공적으로\n저장 되었습니다.", "확 인");
                    } else {
                        Log.e(TAG, call.request().toString());
                        activity.showPopupDialog(response.body().getResult(), response.body().getMessage(), "확 인");
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

    private void saveDeviceData() {
        activity.showProgress();

        Retrofit.PreferencesApi preferencesApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<ResponseDataFormat.SavePreferencesData> call = preferencesApi.exportPreferencesPost(mPrefGlobal.getAuthorization(), Gayo_SharedPreferences.PrefDeviceData.prefItem);

        call.enqueue(new Callback<ResponseDataFormat.SavePreferencesData>() {
            @Override
            public void onResponse(Call<ResponseDataFormat.SavePreferencesData> call, Response<ResponseDataFormat.SavePreferencesData> response) {
                activity.closeProgress();
                if (response.body() != null) {
                    if (response.body().getResult().equals("OK")) {
                        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_SETTING_SUCCESS);
                        activity.showPopupDialog(null, "기기 세팅이 완료되었습니다.", "확 인");
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

    private void getDeviceData() {
        Retrofit.PreferencesApi loginApi = Retrofit.PreferencesApi.retrofit.create(Retrofit.PreferencesApi.class);

        Call<RequestDataFormat.DeviceBody> call = loginApi.loadDeviceDataPost(mPrefGlobal.getAuthorization(), new RequestDataFormat.DeviceInfoBody(etSerialCode.getText().toString(), etBuildingCode.getText().toString())); // test

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
                        ((PreferencesActivity) activity).setFragmentRequested(((PreferencesActivity) activity).REQUEST_SETTING_SUCCESS);
                        activity.showPopupDialog(null, "설정 데이터 불러오기에\n성공하였습니다.", "확 인");

                    } else {
                        activity.showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");
                    }
                } else {
                    activity.showWarningDialog("설정 데이터 불러오기에 실패하였습니다.\n기기 정보 입력 후 저장 버튼을 눌러 세팅을 완료해주세요.", "확 인");

                }

            }

            @Override
            public void onFailure(Call<RequestDataFormat.DeviceBody> call, Throwable t) {
                activity.closeProgress();

            }
        });
    }

    public String getMacAddress(Context context) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : interfaces) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00"; // 기본값 (제한되었을 경우)
    }

    private String EditTextToString(EditText et) {
        return et.getText().toString().trim();
    }
}

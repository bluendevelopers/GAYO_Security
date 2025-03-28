package bluen.homein.gayo_security.activity.preferences.fragment;

import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import butterknife.BindView;
import butterknife.OnClick;

public class NetworkSetFragment extends BaseFragment {

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

    @BindView(R.id.et_allow_ip_1)
    EditText etAllowIp1;

    @BindView(R.id.et_allow_ip_2)
    EditText etAllowIp2;

    @BindView(R.id.et_allow_ip_3)
    EditText etAllowIp3;

    @BindView(R.id.et_allow_ip_4)
    EditText etAllowIp4;


    @OnClick(R.id.lay_root)
    void clickLayRoot() {
        hideAndClearFocus(appContext, etDeviceName);
        hideAndClearFocus(appContext, etIpAddress);
        hideAndClearFocus(appContext, etMacAddress);
        hideAndClearFocus(appContext, etSubnetMask);
        hideAndClearFocus(appContext, etGateway);
        hideAndClearFocus(appContext, etServerIp);
        hideAndClearFocus(appContext, etServerPort);
        hideAndClearFocus(appContext, etAllowIp1);
        hideAndClearFocus(appContext, etAllowIp2);
        hideAndClearFocus(appContext, etAllowIp3);
        hideAndClearFocus(appContext, etAllowIp4);
    }

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {
        if (etDeviceName.getText().toString().isEmpty()) {
            ((PreferencesActivity) activity).showPopupDialog(null, "기기 명을 입력해주세요.", "확 인");
            return;
        }


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
        RequestDataFormat.DeviceNetworkBody deviceNetworkBody = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceNetworkBody();
        if (deviceNetworkBody != null) {
            etDeviceName.setText(deviceNetworkBody.getFacilityName());
            etIpAddress.setText(deviceNetworkBody.getIpAddress());
            etMacAddress.setText(deviceNetworkBody.getMacAddress());
            etSubnetMask.setText(deviceNetworkBody.getSubNet());
            etGateway.setText(deviceNetworkBody.getGateWayIP());
            etServerIp.setText(deviceNetworkBody.getServerIP());
            etServerPort.setText(String.valueOf(deviceNetworkBody.getServerPort()));

            RequestDataFormat.IpAddressBody[] ipAddressBodies = deviceNetworkBody.getIpPermissionArray();

        }
    }


}

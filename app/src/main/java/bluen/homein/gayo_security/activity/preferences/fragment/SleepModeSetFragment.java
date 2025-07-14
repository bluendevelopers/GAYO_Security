package bluen.homein.gayo_security.activity.preferences.fragment;

import android.os.VibrationEffect;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import butterknife.BindView;
import butterknife.OnClick;

public class SleepModeSetFragment extends BaseFragment {

    @BindView(R.id.iv_main_return_clear)
    ImageView ivMainReturnClear;
    @BindView(R.id.iv_main_return_after_30s)
    ImageView ivMainReturnAfter30s;
    @BindView(R.id.iv_main_return_after_60s)
    ImageView ivMainReturnAfter60s;
    @BindView(R.id.iv_main_return_after_90s)
    ImageView ivMainReturnAfter90s;

    @BindView(R.id.iv_sleep_always)
    ImageView ivSleepAlways;
    @BindView(R.id.iv_sleep_after_10s)
    ImageView ivSleepAfter10s;
    @BindView(R.id.iv_sleep_after_30s)
    ImageView ivSleepAfter30s;
    @BindView(R.id.iv_sleep_after_60s)
    ImageView ivSleepAfter60s;
    @BindView(R.id.iv_sleep_after_90s)
    ImageView ivSleepAfter90s;
    int selectedMainReturnTime = 0;
    int selectedSleepModeTime = 0;

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceSleepModeBody(new RequestDataFormat.DeviceSleepModeBody(selectedMainReturnTime, selectedSleepModeTime));
        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
//        activity.setFragmentRequested(activity.SLEEP_MODE_REFRESH);
        activity.setSleepTimeoutSeconds(selectedSleepModeTime);
        activity.setGoToMainSeconds(selectedMainReturnTime);
        activity.showPopupDialog(null, "성공적으로 저장 되었습니다.", "확 인");
    }

    @OnClick({R.id.iv_sleep_always, R.id.iv_sleep_after_10s,
            R.id.iv_sleep_after_30s, R.id.iv_sleep_after_60s, R.id.iv_sleep_after_90s})
    void clickSleepModeView(View view) {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        ivSleepAlways.setImageResource(R.drawable.box_non_select);
        ivSleepAfter10s.setImageResource(R.drawable.box_non_select);
        ivSleepAfter30s.setImageResource(R.drawable.box_non_select);
        ivSleepAfter60s.setImageResource(R.drawable.box_non_select);
        ivSleepAfter90s.setImageResource(R.drawable.box_non_select);

        switch (view.getId()) {

            case R.id.iv_sleep_always:
                ivSleepAlways.setImageResource(R.drawable.box_select);
                selectedSleepModeTime = 0;
                break;

            case R.id.iv_sleep_after_10s:
                ivSleepAfter10s.setImageResource(R.drawable.box_select);
                selectedSleepModeTime = 10;
                break;

            case R.id.iv_sleep_after_30s:
                ivSleepAfter30s.setImageResource(R.drawable.box_select);
                selectedSleepModeTime = 30;
                break;

            case R.id.iv_sleep_after_60s:
                ivSleepAfter60s.setImageResource(R.drawable.box_select);
                selectedSleepModeTime = 60;
                break;
            case R.id.iv_sleep_after_90s:
                ivSleepAfter90s.setImageResource(R.drawable.box_select);
                selectedSleepModeTime = 90;
                break;
        }
    }

    @OnClick({R.id.iv_main_return_clear, R.id.iv_main_return_after_30s,
            R.id.iv_main_return_after_60s, R.id.iv_main_return_after_90s})
    void clickImageView(View view) {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        ivMainReturnClear.setImageResource(R.drawable.box_non_select);
        ivMainReturnAfter30s.setImageResource(R.drawable.box_non_select);
        ivMainReturnAfter60s.setImageResource(R.drawable.box_non_select);
        ivMainReturnAfter90s.setImageResource(R.drawable.box_non_select);

        switch (view.getId()) {
            case R.id.iv_main_return_clear:
                ivMainReturnClear.setImageResource(R.drawable.box_select);
                selectedMainReturnTime = 0;
                break;

            case R.id.iv_main_return_after_30s:
                ivMainReturnAfter30s.setImageResource(R.drawable.box_select);
                selectedMainReturnTime = 30;
                break;

            case R.id.iv_main_return_after_60s:
                ivMainReturnAfter60s.setImageResource(R.drawable.box_select);
                selectedMainReturnTime = 60;
                break;

            case R.id.iv_main_return_after_90s:
                ivMainReturnAfter90s.setImageResource(R.drawable.box_select);
                selectedMainReturnTime = 90;
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_sleep_mode_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {
        RequestDataFormat.DeviceSleepModeBody deviceSleepModeBody = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSleepModeBody();
        if (deviceSleepModeBody != null) {
            selectedMainReturnTime = deviceSleepModeBody.getMainReturnTime();
            selectedSleepModeTime = deviceSleepModeBody.getSleepTime();

            switch (deviceSleepModeBody.getMainReturnTime()) {
                case 0:
                    clickImageView(v.findViewById(R.id.iv_main_return_clear));
                    break;
                case 30:
                    clickImageView(v.findViewById(R.id.iv_main_return_after_30s));
                    break;
                case 60:
                    clickImageView(v.findViewById(R.id.iv_main_return_after_60s));
                    break;
                case 90:
                    clickImageView(v.findViewById(R.id.iv_main_return_after_90s));
                    break;
            }

            switch (deviceSleepModeBody.getSleepTime()) {
                case 0:
                    clickSleepModeView(v.findViewById(R.id.iv_sleep_always));
                    break;
                case 10:
                    clickSleepModeView(v.findViewById(R.id.iv_sleep_after_10s));
                    break;
                case 30:
                    clickSleepModeView(v.findViewById(R.id.iv_sleep_after_30s));
                    break;
                case 60:
                    clickSleepModeView(v.findViewById(R.id.iv_sleep_after_60s));
                    break;
                case 90:
                    clickSleepModeView(v.findViewById(R.id.iv_sleep_after_90s));
                    break;

            }
        }
    }
}

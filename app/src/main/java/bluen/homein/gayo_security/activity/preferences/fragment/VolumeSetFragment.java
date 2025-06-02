package bluen.homein.gayo_security.activity.preferences.fragment;

import android.os.VibrationEffect;
import android.view.View;

import com.github.florent37.androidslidr.Slidr;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import butterknife.BindView;
import butterknife.OnClick;

public class VolumeSetFragment extends BaseFragment {

    @BindView(R.id.slidr_inter_phone_volume)
    Slidr slidrInterPhone;
    @BindView(R.id.slidr_phone)
    Slidr slidrPhone;
    @BindView(R.id.slidr_common)
    Slidr slidrCommon;
    @BindView(R.id.slidr_alarm)
    Slidr slidrAlarm;
    @BindView(R.id.slidr_system)
    Slidr slidrSystem;

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {
//      mPrefGlobal.setBrightness(slidrBrightness.getCurrentValue() / 100f);
        Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceSoundBody(
                new RequestDataFormat.DeviceSoundBody((int) slidrInterPhone.getCurrentValue(), "01", (int) slidrPhone.getCurrentValue(), "01",
                        (int) slidrCommon.getCurrentValue(), "01", (int) slidrAlarm.getCurrentValue(), (int) slidrSystem.getCurrentValue()));
        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
        activity.showPopupDialog(null, "성공적으로 저장 되었습니다.", "확 인");
    }

    @OnClick(R.id.iv_inter_phone_volume_max)
    void clickIvInterPhoneVolumeUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrInterPhone.getCurrentValue() + 1;
        if (upValue >= 10) {
            upValue = 10;
        }
        slidrInterPhone.setCurrentValue(upValue);

    }

    @OnClick(R.id.iv_inter_phone_volume_min)
    void clickIvInterPhoneVolumeDownBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float downValue = slidrInterPhone.getCurrentValue() - 1;
        if (downValue <= 0) {
            downValue = 0;
        }

        slidrInterPhone.setCurrentValue(downValue);
    }

    @OnClick(R.id.iv_inter_phone_left)
    void clickIvInterPhoneBellChangeLeftBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_inter_phone_right)
    void clickIvInterPhoneBellChangeRightBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_phone_volume_max)
    void clickPhoneVolumeUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrPhone.getCurrentValue() + 1;
        if (upValue >= 10) {
            upValue = 10;
        }
        slidrPhone.setCurrentValue(upValue);


    }

    @OnClick(R.id.iv_phone_volume_min)
    void clickPhoneVolumeDownBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float downValue = slidrPhone.getCurrentValue() - 1;

        if (downValue <= 0) {
            downValue = 0;
        }

        slidrPhone.setCurrentValue(downValue);
    }

    @OnClick(R.id.iv_phone_left)
    void clickPhoneBellChangeLeftBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_phone_right)
    void clickPhoneBellChangeRightBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_common_volume_max)
    void clickCommonUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrCommon.getCurrentValue() + 1;
        if (upValue >= 10) {
            upValue = 10;
        }
        slidrCommon.setCurrentValue(upValue);


    }

    @OnClick(R.id.iv_common_volume_min)
    void clickCommonDownBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float downValue = slidrCommon.getCurrentValue() - 1;

        if (downValue <= 0) {
            downValue = 0;
        }

        slidrCommon.setCurrentValue(downValue);

    }


    @OnClick(R.id.iv_common_left)
    void clickCommonBellChangeLeftBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_common_right)
    void clickCommonBellChangeRightBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_alarm_volume_max)
    void clickAlarmUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrAlarm.getCurrentValue() + 1;
        if (upValue >= 10) {
            upValue = 10;
        }
        slidrAlarm.setCurrentValue(upValue);


    }

    @OnClick(R.id.iv_alarm_volume_min)
    void clickAlarmDownBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float downValue = slidrAlarm.getCurrentValue() - 1;

        if (downValue <= 0) {
            downValue = 0;
        }

        slidrAlarm.setCurrentValue(downValue);

    }

    @OnClick(R.id.iv_system_volume_max)
    void clickSystemUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrSystem.getCurrentValue() + 1;
        if (upValue >= 10) {
            upValue = 10;
        }
        slidrSystem.setCurrentValue(upValue);


    }

    @OnClick(R.id.iv_system_volume_min)
    void clickSystemDownBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float downValue = slidrSystem.getCurrentValue() - 1;

        if (downValue <= 0) {
            downValue = 0;
        }

        slidrSystem.setCurrentValue(downValue);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_volume_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {

        sliderUiSet(slidrInterPhone);
        sliderUiSet(slidrPhone);
        sliderUiSet(slidrCommon);
        sliderUiSet(slidrAlarm);
        sliderUiSet(slidrSystem);

        if (Gayo_SharedPreferences.PrefDeviceData.prefItem != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody() != null) {
            slidrInterPhone.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getInterPhoneSound());
            slidrPhone.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getCallSound());
            slidrCommon.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getEntranceSound());
            slidrAlarm.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getNotiSound());
            slidrSystem.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getSystemSound());
        } else {
            //default value
            slidrInterPhone.setCurrentValue(5);
            slidrPhone.setCurrentValue(5);
            slidrCommon.setCurrentValue(5);
            slidrAlarm.setCurrentValue(5);
            slidrSystem.setCurrentValue(5);
        }


    }

    private void sliderUiSet(Slidr _slidr) {
        _slidr.setMax(10);
        _slidr.setMin(0);
//        _slidr.setCurrentValue(5);
        _slidr.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
                return String.format("", (int) value);
            }
        });
    }
}

package bluen.homein.gayo_security.activity.preferences.fragment;

import android.content.Context;
import android.media.AudioManager;
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


    @BindView(R.id.slidr_phone)
    Slidr slidrPhone;
    @BindView(R.id.slidr_alarm)
    Slidr slidrAlarm;
    @BindView(R.id.slidr_system)
    Slidr slidrSystem;
    private AudioManager am;

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {
//      mPrefGlobal.setBrightness(slidrBrightness.getCurrentValue() / 100f);
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceSoundBody(
                new RequestDataFormat.DeviceSoundBody(0, "01", (int) slidrPhone.getCurrentValue(), "01",
                        0, "01", (int) slidrAlarm.getCurrentValue(), (int) slidrSystem.getCurrentValue()));
        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);

        am.setStreamVolume(
                AudioManager.STREAM_RING,
                (int) slidrPhone.getCurrentValue(),
                0
        );
        am.setStreamVolume(
                AudioManager.STREAM_ALARM,
                (int) slidrAlarm.getCurrentValue(),
                0
        );
        am.setStreamVolume(
                AudioManager.STREAM_SYSTEM,
                (int) slidrSystem.getCurrentValue(),
                0
        );
        activity.showPopupDialog(null, "성공적으로 저장 되었습니다.",  getString(R.string.confirm));


    }

    @OnClick(R.id.iv_phone_volume_max)
    void clickCallVolumeUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrPhone.getCurrentValue() + 1;
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        if (upValue >= maxVol) {
            upValue = maxVol;
        }
        slidrPhone.setCurrentValue(upValue);


    }

    @OnClick(R.id.iv_phone_volume_min)
    void clickCallVolumeDownBtn() {
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


    @OnClick(R.id.iv_alarm_volume_max)
    void clickAlarmUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrAlarm.getCurrentValue() + 1;
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        if (upValue >= maxVol) {
            upValue = maxVol;
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
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);

        if (upValue >= maxVol) {
            upValue = maxVol;
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
        am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        sliderUiSet(slidrPhone, am.getStreamMaxVolume(AudioManager.STREAM_RING));
        sliderUiSet(slidrAlarm, am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        sliderUiSet(slidrSystem, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));

        if (Gayo_SharedPreferences.PrefDeviceData.prefItem != null && Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody() != null) {
            slidrPhone.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getCallSound());
            slidrAlarm.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getAlarmSound());
            slidrSystem.setCurrentValue(Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceSoundBody().getSystemSound());
        } else {
            //default value
            slidrPhone.setCurrentValue(7);
            slidrAlarm.setCurrentValue(7);
            slidrSystem.setCurrentValue(7);
        }


    }

    private void sliderUiSet(Slidr _slidr, int maxVol) {
        _slidr.setMax(maxVol);
        _slidr.setMin(0);
        _slidr.setListener(new Slidr.Listener() {
            @Override
            public void valueChanged(Slidr slidr, float newValue) {
                int newIntValue = (int) newValue;
                _slidr.setCurrentValue(newIntValue);
            }

            @Override
            public void bubbleClicked(Slidr slidr) {

            }
        });
        _slidr.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
                return String.format("", (int) value);
            }
        });
    }
}

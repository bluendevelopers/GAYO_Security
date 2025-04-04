package bluen.homein.gayo_security.activity.preferences.fragment;

import android.os.VibrationEffect;
import android.view.View;

import com.github.florent37.androidslidr.Slidr;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.PreferencesActivity;
import bluen.homein.gayo_security.base.BaseFragment;
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

    @OnClick(R.id.iv_inter_phone_volume_max)
    void clickIvInterPhoneVolumeUpBtn() {
        ((PreferencesActivity) activity).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_inter_phone_volume_min)
    void clickIvInterPhoneVolumeDownBtn() {
        ((PreferencesActivity) activity).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

    }

    @OnClick(R.id.iv_inter_phone_left)
    void clickIvInterPhoneBellChangeLeftBtn() {
        ((PreferencesActivity) activity).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code
    }

    @OnClick(R.id.iv_inter_phone_right)
    void clickIvInterPhoneBellChangeRightBtn() {
        ((PreferencesActivity) activity).vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        //code

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


    }

    private void sliderUiSet(Slidr _slidr) {
        _slidr.setMax(10);
        _slidr.setMin(0);
        _slidr.setCurrentValue(5);
        _slidr.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
                return String.format("", (int) value);
            }
        });
    }
}

package bluen.homein.gayo_security.activity.preferences.fragment;

import android.os.VibrationEffect;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.github.florent37.androidslidr.Slidr;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseFragment;
import bluen.homein.gayo_security.preference.Gayo_SharedPreferences;
import bluen.homein.gayo_security.rest.RequestDataFormat;
import butterknife.BindView;
import butterknife.OnClick;

public class ScreenSetFragment extends BaseFragment {

    @BindView(R.id.slidr_brightness)
    Slidr slidrBrightness;

    @OnClick(R.id.tv_save_btn)
    void clickSaveBtn() {
//        mPrefGlobal.setBrightness(slidrBrightness.getCurrentValue() / 100f);
        Gayo_SharedPreferences.PrefDeviceData.prefItem.setDeviceUIBody(new RequestDataFormat.DeviceUIBody((int) slidrBrightness.getCurrentValue(), "L"));
        Gayo_SharedPreferences.PrefDeviceData.setPrefDeviceData(appContext, Gayo_SharedPreferences.PrefDeviceData.prefItem);
        activity.showPopupDialog(null, "성공적으로 저장 되었습니다.", "확 인");
    }

    @OnClick(R.id.iv_bright_min)
    void clickBrightnessDownBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float downValue = slidrBrightness.getCurrentValue() - 10;

        if (downValue <= 0) {
            downValue = 0;
        }

        slidrBrightness.setCurrentValue(downValue);

    }

    @OnClick(R.id.iv_bright_max)
    void clickBrightnessUpBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        float upValue = slidrBrightness.getCurrentValue() + 10;
        if (upValue >= 100) {
            upValue = 100;
        }
        slidrBrightness.setCurrentValue(upValue);


    }

    @OnClick(R.id.iv_left)
    void clickScreenChangeLeftBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    @OnClick(R.id.iv_right)
    void clickScreenChangeRightBtn() {
        activity.vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {

        slidrBrightness.setMax(100);
        slidrBrightness.setMin(0);

//        float savedBrightness = mPrefGlobal.getBrightness();
        float savedBrightness = Gayo_SharedPreferences.PrefDeviceData.prefItem.getDeviceUIBody().getBrightness();
        slidrBrightness.setCurrentValue(savedBrightness);

        slidrBrightness.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
//                return String.format("%d%%", (int) value);
                return String.format("", (int) value);
            }
        });
        slidrBrightness.setListener(new Slidr.Listener() {
            @Override
            public void valueChanged(Slidr slidr, float currentValue) {
                float brightnessValue = currentValue;
                setScreenBrightness(brightnessValue / 100f);
//                AppBrightnessManager.saveBrightness(getActivity(), brightnessValue); // 변경할 때마다 저장
//                mPrefGlobal.setBrightness(brightnessValue);
            }

            @Override
            public void bubbleClicked(Slidr slidr) {

            }
        });


    }


}

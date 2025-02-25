package bluen.homein.gayo_security.activity.preferences.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.github.florent37.androidslidr.Slidr;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseFragment;
import butterknife.BindView;

public class ScreenSetFragment extends BaseFragment {

    @BindView(R.id.slidr_brightness)
    Slidr slidrBrightness;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_set;
    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initFragmentView(View v) {

        // 밝기 조절
        slidrBrightness.setMax(100);
        slidrBrightness.setMin(0);
        slidrBrightness.setCurrentValue(5);
        slidrBrightness.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
                return String.format("", (int) value);
            }
        });
    }
}

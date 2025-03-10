package bluen.homein.gayo_security.activity.preferences;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.activity.preferences.fragment.DataSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.NetworkSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.PatrolModeSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.ScreenSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.SleepModeSetFragment;
import bluen.homein.gayo_security.activity.preferences.fragment.VolumeSetFragment;
import bluen.homein.gayo_security.base.BaseActivity;
import bluen.homein.gayo_security.databinding.FragmentDataSetBinding;
import bluen.homein.gayo_security.databinding.FragmentNetworkSetBinding;
import bluen.homein.gayo_security.databinding.FragmentPatrolModeSetBinding;
import bluen.homein.gayo_security.databinding.FragmentSleepModeSetBinding;
import bluen.homein.gayo_security.databinding.FragmentVolumeSetBinding;
import bluen.homein.gayo_security.dialog.PopupDialog;
import butterknife.BindView;
import butterknife.OnClick;

public class PreferencesActivity extends BaseActivity {


    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.tv_screen_set_btn)
    TextView tvScreenSetBtn;
    @BindView(R.id.tv_volume_set_btn)
    TextView tvVolumeSetBtn;
    @BindView(R.id.tv_sleep_mode_btn)
    TextView tvSleepModeBtn;
    @BindView(R.id.tv_network_set_btn)
    TextView tvNetworkSetBtn;
    @BindView(R.id.tv_data_set_btn)
    TextView tvDataSetBtn;
    @BindView(R.id.tv_patrol_mode_btn)
    TextView tvPatrolModeBtn;
    @BindView(R.id.lay_container)
    FrameLayout layContainer;

    private FragmentDataSetBinding fragmentDataSetBinding;
    private FragmentNetworkSetBinding fragmentNetworkSetBinding;
    private FragmentPatrolModeSetBinding fragmentPatrolModeSetBinding;
    private FragmentSleepModeSetBinding fragmentSleepModeSetBinding;
    private FragmentVolumeSetBinding fragmentVolumeSetBinding;
    private TextView[] textViewArray;

    private void clearButtonUi(TextView tv) {
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundTintList(null);
    }

    @OnClick(R.id.lay_home_btn)
    void clickHomeBtn() {
        finish();
    }

    @OnClick({R.id.tv_screen_set_btn, R.id.tv_volume_set_btn, R.id.tv_sleep_mode_btn,
            R.id.tv_network_set_btn, R.id.tv_data_set_btn, R.id.tv_patrol_mode_btn})
    void onTextViewClicked(View view) {
        TextView clickedTextView = (TextView) view;

        for (TextView tv : textViewArray) {
            if (tv != clickedTextView) {
                clearButtonUi(tv);
            }
        }

        int mainColor = ContextCompat.getColor(this, R.color.main_color);

        clickedTextView.setTextColor(Color.WHITE);
        clickedTextView.setBackgroundTintList(ColorStateList.valueOf(mainColor));

        Fragment selectedFragment = null;

        switch (view.getId()) {
            case R.id.tv_screen_set_btn:
                selectedFragment = new ScreenSetFragment();
                tvPageTitle.setText("사용자 설정 _ 화면 설정");
                break;
            case R.id.tv_volume_set_btn:
                selectedFragment = new VolumeSetFragment();
                tvPageTitle.setText("사용자 설정 _ 음량 조절");
                break;
            case R.id.tv_sleep_mode_btn:
                selectedFragment = new SleepModeSetFragment();
                tvPageTitle.setText("사용자 설정 _ 슬립모드 설정");
                break;
            case R.id.tv_network_set_btn:
                selectedFragment = new NetworkSetFragment();
                tvPageTitle.setText("사용자 설정 _ 네트워크 설정");
                break;
            case R.id.tv_data_set_btn:
                selectedFragment = new DataSetFragment();
                tvPageTitle.setText("사용자 설정 _ 데이터 설정");
                break;
            case R.id.tv_patrol_mode_btn:
                selectedFragment = new PatrolModeSetFragment();
                tvPageTitle.setText("사용자 설정 _ 순찰모드 설정");
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.lay_container, selectedFragment)
                    .commit();
        }
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

    }


    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_preferences;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        hideNavigationBar();
        textViewArray = new TextView[]{tvScreenSetBtn, tvVolumeSetBtn, tvSleepModeBtn, tvNetworkSetBtn, tvDataSetBtn, tvPatrolModeBtn};

        popupDialog.onCallBack(new PopupDialog.DialogCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onNextStep() {

            }
        });

        Fragment selectedFragment = new ScreenSetFragment();

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.lay_container, selectedFragment)
                    .commit();
        }
    }

}